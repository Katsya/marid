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

package org.marid.ide.panes.filebrowser;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.marid.ide.project.ProjectManager;
import org.marid.ide.project.ProjectProfile;
import org.marid.l10n.L10nSupport;
import org.marid.spring.xml.MaridBeanUtils;
import org.marid.spring.xml.data.BeanFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toMap;
import static org.marid.jfx.icons.FontIcon.D_FILE;
import static org.marid.jfx.icons.FontIcon.D_FOLDER;
import static org.marid.jfx.icons.FontIcons.glyphIcon;
import static org.marid.misc.Builder.build;
import static org.marid.spring.xml.MaridBeanUtils.isFile;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanFileBrowserTree extends TreeTableView<Path> implements L10nSupport {

    final ObservableValue<ProjectProfile> projectProfileObservableValue;

    @Autowired
    public BeanFileBrowserTree(ProjectManager projectManager) {
        this(projectManager.profileProperty());
    }

    protected BeanFileBrowserTree(ObservableValue<ProjectProfile> projectProfileObservableValue) {
        super(new TreeItem<>(projectProfileObservableValue.getValue().getBeansDirectory(), glyphIcon(D_FOLDER, 16)));
        this.projectProfileObservableValue = projectProfileObservableValue;
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setTableMenuButtonVisible(true);
        final MapChangeListener<Path, BeanFile> filesChangeListener = change -> {
            if (change.wasAdded()) {
                add(change.getKey());
            }
            if (change.wasRemoved()) {
                remove(change.getKey());
            }
        };
        projectProfileObservableValue.addListener((observable, oldValue, newValue) -> {
            oldValue.getBeanFiles().removeListener(filesChangeListener);
            newValue.getBeanFiles().addListener(filesChangeListener);
            setRoot(new TreeItem<>(newValue.getBeansDirectory(), glyphIcon(D_FOLDER, 16)));
            newValue.getBeanFiles().keySet().forEach(this::add);
        });
        getProfile().getBeanFiles().addListener(filesChangeListener);
        getProfile().getBeanFiles().keySet().forEach(this::add);
        getColumns().add(build(new TreeTableColumn<Path, String>(), col -> {
            col.setText(s("File"));
            col.setPrefWidth(600);
            col.setMaxWidth(2000);
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getFileName().toString()));
        }));
        getColumns().add(build(new TreeTableColumn<Path, FileTime>(), col -> {
            col.setText(s("Date"));
            col.setPrefWidth(250);
            col.setMaxWidth(300);
            col.setStyle("-fx-alignment: baseline-right");
            col.setCellValueFactory(param -> {
                final Path path = param.getValue().getValue();
                try {
                    return new SimpleObjectProperty<>(Files.getLastModifiedTime(path));
                } catch (IOException x) {
                    return null;
                }
            });
        }));
        getColumns().add(build(new TreeTableColumn<Path, Integer>(), col -> {
            col.setText(s("Bean count"));
            col.setPrefWidth(250);
            col.setMaxWidth(250);
            col.setStyle("-fx-alignment: baseline-right");
            col.setCellValueFactory(param -> {
                final Path path = param.getValue().getValue();
                return new SimpleObjectProperty<>(projectProfileObservableValue.getValue().getBeanFiles().entrySet().stream()
                        .filter(e -> e.getKey().startsWith(path))
                        .mapToInt(e -> e.getValue().beans.size())
                        .sum());
            });
        }));
        setTreeColumn(getColumns().get(0));
    }

    public ProjectProfile getProfile() {
        return projectProfileObservableValue.getValue();
    }

    private void add(Path path) {
        final Path base = getProfile().getBeansDirectory();
        if (!path.startsWith(base)) {
            return;
        }
        final Path relative = base.relativize(path);
        final AtomicReference<TreeItem<Path>> itemRef = new AtomicReference<>(getRoot());
        for (int i = 1; i <= relative.getNameCount(); i++) {
            final Path suffix = relative.subpath(0, i);
            final Path p = base.resolve(suffix);
            itemRef.set(itemRef.get().getChildren()
                    .stream()
                    .filter(e -> e.getValue().equals(p))
                    .findAny()
                    .orElseGet(() -> {
                        final TreeItem<Path> newItem = new TreeItem<>(p, glyphIcon(isFile(p) ? D_FILE : D_FOLDER, 16));
                        itemRef.get().getChildren().add(newItem);
                        itemRef.get().getChildren().sort(Comparator.comparing(TreeItem::getValue));
                        itemRef.get().setExpanded(true);
                        return newItem;
                    }));
        }
    }

    private void remove(Path path) {
        final Path base = getProfile().getBeansDirectory();
        if (!path.startsWith(base)) {
            return;
        }
        final Path relative = base.relativize(path);
        final AtomicReference<TreeItem<Path>> itemRef = new AtomicReference<>(getRoot());
        for (int i = 1; i <= relative.getNameCount(); i++) {
            final Path suffix = relative.subpath(0, i);
            final Path p = base.resolve(suffix);
            itemRef.set(itemRef.get().getChildren()
                    .stream()
                    .filter(e -> e.getValue().equals(p))
                    .findAny()
                    .orElse(null));
            if (itemRef.get() == null) {
                break;
            }
        }
        if (itemRef.get() != null) {
            final TreeItem<Path> parent = itemRef.get().getParent();
            parent.getChildren().remove(itemRef.get());
            for (TreeItem<Path> i = parent, p = i.getParent(); p != null; i = p, p = i.getParent()) {
                if (i.getChildren().isEmpty()) {
                    p.getChildren().remove(i);
                }
            }
        }
    }

    public void onFileAdd(ActionEvent event) {
        final TextInputDialog dialog = new TextInputDialog("file");
        dialog.setTitle(s("New file"));
        dialog.setHeaderText(s("Enter file name") + ":");
        final Optional<String> value = dialog.showAndWait();
        if (value.isPresent()) {
            final String name = value.get().endsWith(".xml") ? value.get() : value.get() + ".xml";
            final TreeItem<Path> item = getSelectionModel().getSelectedItem();
            final Path path = item.getValue().resolve(name);
            getProfile().getBeanFiles().put(path, new BeanFile());
        }
    }

    public BooleanBinding fileAddDisabled() {
        return Bindings.createBooleanBinding(() -> {
            final TreeItem<Path> item = getSelectionModel().getSelectedItem();
            if (item == null) {
                return true;
            }
            if (item.getValue().getFileName().toString().endsWith(".xml")) {
                return true;
            }
            return false;
        }, getSelectionModel().selectedItemProperty());
    }

    public void onDirAdd(ActionEvent event) {
        final TextInputDialog dialog = new TextInputDialog("directory");
        dialog.setTitle(s("New directory"));
        dialog.setHeaderText(s("Enter directory name") + ":");
        final Optional<String> value = dialog.showAndWait();
        if (value.isPresent()) {
            if (value.get().endsWith(".xml")) {
                final Alert alert = new Alert(AlertType.ERROR, m("Directory ends with .xml"), ButtonType.CLOSE);
                alert.setHeaderText(m("Directory creation error"));
                alert.showAndWait();
            } else {
                final TreeItem<Path> item = getSelectionModel().getSelectedItem();
                final Path path = item.getValue().resolve(value.get());
                final TreeItem<Path> newItem = new TreeItem<>(path, glyphIcon(D_FOLDER, 16));
                item.getChildren().add(newItem);
                item.setExpanded(true);
            }
        }
    }

    public void onRename(ActionEvent event) {
        final TreeItem<Path> item = getSelectionModel().getSelectedItem();
        final Path path = item.getValue();
        final boolean file = isFile(path);
        final String fileName = path.getFileName().toString();
        final String defaultValue = file ? fileName.substring(0, fileName.length() - 4) : fileName;
        final TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(file ? s("Rename file") : s("Rename directory"));
        dialog.setHeaderText(file ? s("Enter a new file name") : s("Enter a new file name"));
        final Optional<String> value = dialog.showAndWait();
        if (value.isPresent()) {
            if (file) {
                final Path newPath = path.getParent().resolve(value.get().endsWith(".xml") ? value.get() : value.get() + ".xml");
                final BeanFile beanFile = getProfile().getBeanFiles().remove(path);
                getProfile().getBeanFiles().put(newPath, beanFile);
            } else {
                if (value.get().endsWith(".xml")) {
                    final Alert alert = new Alert(AlertType.ERROR, m("Directory ends with .xml"), ButtonType.CLOSE);
                    alert.setHeaderText(m("Directory creation error"));
                    alert.showAndWait();
                } else {
                    final Map<Path, BeanFile> relativeMap = getProfile().getBeanFiles().entrySet()
                            .stream()
                            .filter(e -> e.getKey().startsWith(path))
                            .collect(toMap(e -> path.relativize(e.getKey()), Map.Entry::getValue));
                    getProfile().getBeanFiles().keySet().removeIf(p -> p.startsWith(path));
                    final Path newPath = path.getParent().resolve(value.get());
                    relativeMap.forEach((p, beanFile) -> getProfile().getBeanFiles().put(newPath.resolve(p), beanFile));
                }
            }
        }
    }

    public void onDelete(ActionEvent event) {
        final TreeItem<Path> item = getSelectionModel().getSelectedItem();
        final Path path = item.getValue();
        if (isFile(path)) {
            getProfile().getBeanFiles().remove(path);
        } else {
            getProfile().getBeanFiles().keySet().forEach(p -> {
                if (p.startsWith(path)) {
                    getProfile().getBeanFiles().remove(p);
                }
            });
        }
    }

    public BooleanBinding moveDisabled() {
        return Bindings.createBooleanBinding(() -> {
            final TreeItem<Path> item = getSelectionModel().getSelectedItem();
            return item == null || item == getRoot();
        }, getSelectionModel().selectedItemProperty());
    }

    public BooleanBinding launchDisabled() {
        return Bindings.createBooleanBinding(() -> {
            final TreeItem<Path> item = getSelectionModel().getSelectedItem();
            return item == null || !MaridBeanUtils.isFile(item.getValue());
        }, getSelectionModel().selectedItemProperty());
    }
}