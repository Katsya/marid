/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.ide.project;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.marid.dependant.project.config.CommonTab;
import org.marid.logging.LogSupport;
import org.marid.spring.xml.*;
import org.springframework.core.ResolvableType;

import javax.annotation.Nonnull;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;
import static org.marid.util.Reflections.parameterName;
import static org.springframework.core.ResolvableType.*;

/**
 * @author Dmitry Ovchinnikov
 */
public class ProjectProfile implements LogSupport, Observable {

    final Model model;
    final Path path;
    final Path pomFile;
    final Path src;
    final Path target;
    final Path srcMain;
    final Path srcTest;
    final Path srcMainJava;
    final Path srcMainResources;
    final Path srcTestJava;
    final Path srcTestResources;
    final Path beansDirectory;
    final Path repository;
    final Logger logger;
    final ObservableList<Pair<Path, BeanFile>> beanFiles;
    final ProjectCacheEntry cacheEntry;
    final BooleanProperty hmi;

    private final List<InvalidationListener> invalidationListeners = new CopyOnWriteArrayList<>();

    ProjectProfile(String name) {
        path = Paths.get(USER_HOME, "marid", "profiles", name);
        pomFile = path.resolve("pom.xml");
        src = path.resolve("src");
        target = path.resolve("target");
        srcMain = src.resolve("main");
        srcTest = src.resolve("test");
        srcMainJava = srcMain.resolve("java");
        srcMainResources = srcMain.resolve("resources");
        srcTestJava = srcTest.resolve("java");
        srcTestResources = srcTest.resolve("resources");
        beansDirectory = srcMainResources.resolve("META-INF").resolve("marid");
        repository = path.resolve(".repo");
        logger = Logger.getLogger(getName());
        model = loadModel();
        model.setModelVersion("4.0.0");
        createFileStructure();
        beanFiles = loadBeanFiles();
        init();
        cacheEntry = new ProjectCacheEntry(this);
        hmi = new SimpleBooleanProperty(isHmi());
        hmi.addListener((observable, oldValue, newValue) -> setHmi(newValue));
    }

    public URLClassLoader getClassLoader() {
        return cacheEntry.getClassLoader();
    }

    public boolean containsBean(String name) {
        return beanFiles.stream()
                .map(Pair::getValue)
                .anyMatch(f -> f.allBeans().anyMatch(b -> b.nameProperty().isEqualTo(name).get()));
    }

    public String generateBeanName(String name) {
        while (containsBean(name)) {
            name += "_new";
        }
        return name;
    }

    void update() throws Exception {
        cacheEntry.update();
        invalidationListeners.forEach(listener -> {
            ResolvableType.clearCache();
            Platform.runLater(() -> listener.invalidated(this));
        });
    }

    private void init() {
        if (model.getProfiles().stream().noneMatch(p -> "conf".equals(p.getId()))) {
            final Profile profile = new Profile();
            profile.setId("conf");
            model.getProfiles().add(profile);
        }
    }

    public BooleanProperty hmiProperty() {
        return hmi;
    }

    public boolean isHmi() {
        return model.getDependencies().stream().anyMatch(CommonTab::isHmi);
    }

    private boolean setHmi(boolean hmi) {
        if (hmi) {
            if (model.getDependencies().stream().anyMatch(CommonTab::isHmi)) {
                return false;
            } else {
                model.getDependencies().removeIf(CommonTab::isRuntime);
                final Dependency dependency = new Dependency();
                dependency.setGroupId("org.marid");
                dependency.setArtifactId("marid-hmi");
                dependency.setVersion("${marid.runtime.version}");
                model.getDependencies().add(dependency);
                return true;
            }
        } else {
            if (model.getDependencies().stream().anyMatch(CommonTab::isRuntime)) {
                return false;
            } else {
                model.getDependencies().removeIf(CommonTab::isHmi);
                final Dependency dependency = new Dependency();
                dependency.setGroupId("org.marid");
                dependency.setArtifactId("marid-runtime");
                dependency.setVersion("${marid.runtime.version}");
                model.getDependencies().add(dependency);
                return true;
            }
        }
    }

    private Model loadModel() {
        try (final InputStream is = Files.newInputStream(pomFile)) {
            final MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(is);
        } catch (NoSuchFileException x) {
            log(FINE, "There is no {0} file", x.getFile());
        } catch (IOException x) {
            log(WARNING, "Unable to read pom.xml", x);
        } catch (XmlPullParserException x) {
            log(WARNING, "Unable to parse pom.xml", x);
        }
        final Model model = new Model();
        model.setOrganization(new Organization());
        model.setName(getName());
        model.setArtifactId(getName());
        model.setGroupId("org.myproject");
        model.setVersion("1.0-SNAPSHOT");
        return model;
    }

    private ObservableList<Pair<Path, BeanFile>> loadBeanFiles() {
        final ObservableList<Pair<Path, BeanFile>> list = FXCollections.observableArrayList();
        try (final Stream<Path> stream = Files.walk(beansDirectory)) {
            stream.filter(p -> p.getFileName().toString().endsWith(".xml"))
                    .map(p -> {
                        try {
                            return new Pair<>(p, MaridBeanDefinitionLoader.load(p));
                        } catch (Exception x) {
                            log(WARNING, "Unable to load {0}", x, p);
                            return new Pair<>(p, new BeanFile());
                        }
                    })
                    .forEach(list::add);
        } catch (IOException x) {
            log(WARNING, "Unable to load bean files", x);
        } catch (Exception x) {
            log(SEVERE, "Unknown error", x);
        }
        return list;
    }

    public ObservableList<Pair<Path, BeanFile>> getBeanFiles() {
        return beanFiles;
    }

    public Model getModel() {
        return model;
    }

    public Path getPath() {
        return path;
    }

    public Path getPomFile() {
        return pomFile;
    }

    public Path getRepository() {
        return repository;
    }

    public Path getBeansDirectory() {
        return beansDirectory;
    }

    public Path getSrc() {
        return src;
    }

    public Path getSrcMainResources() {
        return srcMainResources;
    }

    public Path getTarget() {
        return target;
    }

    public String getName() {
        return path.getFileName().toString();
    }

    @Nonnull
    public Logger logger() {
        return logger;
    }

    public Optional<Class<?>> getClass(String type) {
        return cacheEntry.getClass(type);
    }

    private void createFileStructure() {
        try {
            for (final Path dir : asList(srcMainJava, beansDirectory, srcTestJava, srcTestResources)) {
                Files.createDirectories(dir);
            }
            final Path loggingProperties = srcMainResources.resolve("logging.properties");
            if (Files.notExists(loggingProperties)) {
                final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
                try (final InputStream is = currentLoader.getResourceAsStream("logging/default.properties")) {
                    Files.copy(is, loggingProperties);
                }
            }
        } catch (Exception x) {
            log(WARNING, "Unable to create file structure", x);
        }
    }

    private void savePomFile() {
        try (final OutputStream os = Files.newOutputStream(pomFile)) {
            final MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(os, model);
        } catch (IOException x) {
            log(WARNING, "Unable to save {0}", x, pomFile);
        }
    }

    private void saveBeanFiles() {
        try {
            FileUtils.cleanDirectory(getBeansDirectory().toFile());
        } catch (IOException x) {
            log(WARNING, "Unable to clean beans directory", x);
            return;
        }
        for (final Pair<Path, BeanFile> entry : beanFiles) {
            try {
                Files.createDirectories(entry.getKey().getParent());
                MaridBeanDefinitionSaver.write(entry.getKey(), entry.getValue());
            } catch (Exception x) {
                log(WARNING, "Unable to save {0}", x, entry.getKey());
            }
        }
    }

    public void save() {
        createFileStructure();
        savePomFile();
        saveBeanFiles();
    }

    void delete() {
        try {
            cacheEntry.close();
            FileUtils.deleteDirectory(path.toFile());
        } catch (Exception x) {
            log(WARNING, "Unable to delete {0}", x, getName());
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProjectProfile && (((ProjectProfile) obj).getName().equals(this.getName()));
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListeners.remove(listener);
    }

    public Optional<BeanData> findBean(String name) {
        return getBeanFiles().stream()
                .map(Pair::getValue)
                .flatMap(BeanFile::allBeans)
                .filter(b -> name.equals(b.getName()))
                .findAny();
    }

    public Optional<Class<?>> getClass(BeanData data) {
        if (data.isFactoryBean()) {
            return getConstructor(data).map(e -> ((Method) e).getReturnType());
        } else {
            return getClass(data.type.get());
        }
    }

    public Stream<? extends Executable> getConstructors(BeanData data) {
        if (data.isFactoryBean()) {
            if (data.factoryBean.isNotEmpty().get()) {
                return getBeanFiles().stream()
                        .flatMap(e -> e.getValue().allBeans())
                        .filter(b -> data.factoryBean.isEqualTo(b.nameProperty()).get())
                        .map(this::getClass)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .flatMap(t -> of(t.getMethods()))
                        .filter(m -> m.getReturnType() != void.class)
                        .filter(m -> data.factoryMethod.isEqualTo(m.getName()).get())
                        .sorted(comparingInt(Method::getParameterCount));
            } else {
                return getClass(data.type.get())
                        .map(type -> of(type.getMethods())
                                .filter(m -> Modifier.isStatic(m.getModifiers()))
                                .filter(m -> m.getReturnType() != void.class)
                                .filter(m -> data.factoryMethod.isEqualTo(m.getName()).get())
                                .sorted(comparingInt(Method::getParameterCount)))
                        .orElse(Stream.empty());
            }
        } else {
            return getClass(data)
                    .map(c -> of(c.getConstructors()).sorted(comparingInt(Constructor::getParameterCount)))
                    .orElseGet(Stream::empty);
        }
    }

    public Optional<? extends Executable> getConstructor(BeanData data) {
        final List<? extends Executable> executables = getConstructors(data).collect(toList());
        switch (executables.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(executables.get(0));
            default:
                final Class<?>[] types = data.beanArgs.stream()
                        .map(a -> getClass(a.type.get()).orElse(Object.class))
                        .toArray(Class<?>[]::new);
                return executables.stream().filter(m -> Arrays.equals(types, m.getParameterTypes())).findFirst();
        }
    }

    public void updateBeanDataConstructorArgs(BeanData data, Parameter[] parameters) {
        final List<BeanProp> args = of(parameters)
                .map(p -> {
                    final Optional<BeanProp> found = data.beanArgs.stream()
                            .filter(a -> a.name.isEqualTo(parameterName(p)).get())
                            .findFirst();
                    if (found.isPresent()) {
                        found.get().type.set(p.getType().getName());
                        return found.get();
                    } else {
                        final BeanProp arg = new BeanProp();
                        arg.name.set(parameterName(p));
                        arg.type.set(p.getType().getName());
                        return arg;
                    }
                })
                .collect(toList());
        data.beanArgs.clear();
        data.beanArgs.addAll(args);
    }

    public void updateBeanData(BeanData data) {
        final Class<?> type = getClass(data).orElse(null);
        if (type == null) {
            return;
        }
        final List<Executable> executables = getConstructors(data).collect(toList());
        if (!executables.isEmpty()) {
            if (executables.size() == 1) {
                updateBeanDataConstructorArgs(data, executables.get(0).getParameters());
            } else {
                final Optional<? extends Executable> executable = getConstructor(data);
                executable.ifPresent(e -> updateBeanDataConstructorArgs(data, e.getParameters()));
            }
        }

        final List<PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(data).collect(toList());
        final Map<String, BeanProp> pmap = data.properties.stream().collect(toMap(e -> e.name.get(), e -> e));
        data.properties.clear();
        for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            final BeanProp prop = pmap.computeIfAbsent(propertyDescriptor.getName(), n -> {
                final BeanProp property = new BeanProp();
                property.name.set(n);
                return property;
            });
            prop.type.set(propertyDescriptor.getPropertyType().getName());
            data.properties.add(prop);
        }
    }

    public Stream<PropertyDescriptor> getPropertyDescriptors(BeanData data) {
        final Class<?> type = getClass(data).orElse(Object.class);
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(type);
            return of(beanInfo.getPropertyDescriptors())
                    .filter(d -> d.getWriteMethod() != null);
        } catch (IntrospectionException x) {
            return Stream.empty();
        }
    }

    public ResolvableType getType(BeanData beanData) {
        if (beanData.isFactoryBean()) {
            return getConstructor(beanData).map(e -> forMethodReturnType((Method) e)).orElse(NONE);
        } else {
            return getClass(beanData.type.get()).map(ResolvableType::forClass).orElse(NONE);
        }
    }

    public ResolvableType getArgType(BeanData beanData, String name) {
        return getConstructor(beanData)
                .flatMap(e -> of(e.getParameters()).filter(p -> parameterName(p).equals(name)).findAny())
                .map(p -> ResolvableType.forType(p.getParameterizedType()))
                .orElse(NONE);
    }

    public ResolvableType getPropType(BeanData beanData, String name) {
        return getPropertyDescriptors(beanData)
                .filter(d -> d.getName().equals(name))
                .findAny()
                .map(p -> forMethodParameter(p.getWriteMethod(), 0))
                .orElse(NONE);
    }
}
