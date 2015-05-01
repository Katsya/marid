/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
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

package org.marid.ide.components;

import org.marid.ide.profile.Profile;
import org.marid.jmx.MaridBeanConnectionManager;
import org.marid.logging.LogSupport;
import org.marid.pref.SysPrefSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class ProfileManager implements LogSupport, SysPrefSupport {

    protected final MaridBeanConnectionManager connectionManager;
    protected final ConcurrentSkipListMap<String, Profile> profileMap = new ConcurrentSkipListMap<>();
    protected final List<Consumer<Profile>> addProfileConsumers = new CopyOnWriteArrayList<>();
    protected final List<Consumer<Profile>> removeProfileConsumers = new CopyOnWriteArrayList<>();

    @Autowired
    public ProfileManager(MaridBeanConnectionManager connectionManager) throws IOException {
        this.connectionManager = connectionManager;
        final Path profilesDir = getProfilesDir();
        try (final Stream<Path> stream = Files.list(profilesDir)) {
            stream.filter(Files::isDirectory).map(this::newProfile).forEach(p -> info("Added profile {0}", p));
        }
        if (profileMap.isEmpty()) {
            addProfile("default");
        }
    }

    public void addProfileAddConsumer(Consumer<Profile> consumer) {
        addProfileConsumers.add(consumer);
    }

    public void removeProfileAddConsumer(Consumer<Profile> consumer) {
        addProfileConsumers.remove(consumer);
    }

    public void addProfileRemoveConsumer(Consumer<Profile> consumer) {
        removeProfileConsumers.add(consumer);
    }

    public void removeProfileRemoveConsumer(Consumer<Profile> consumer) {
        removeProfileConsumers.remove(consumer);
    }

    private Profile newProfile(Path path) {
        return profileMap.computeIfAbsent(path.getFileName().toString(), n -> {
            final Profile profile = new Profile(this, path);
            addProfileConsumers.forEach(c -> c.accept(profile));
            return profile;
        });
    }

    protected Path defaultPath() {
        return Paths.get(System.getProperty("user.home"), "marid", "profiles");
    }

    public Path getProfilesDir() {
        final Path path = getSysPref("profilesDir", defaultPath());
        if (!Files.isDirectory(path)) {
            try {
                Files.createDirectories(path);

            } catch (IOException x) {
                throw new IllegalStateException(x);
            }
        }
        return path;
    }

    public Profile addProfile(String name) {
        return newProfile(getProfilesDir().resolve(name));
    }

    public void removeProfile(String name) {
        profileMap.computeIfPresent(name, (n, v) -> {
            removeProfileConsumers.forEach(c -> c.accept(v));
            return null;
        });
    }

    public Profile getProfileByName(String name) {
        return profileMap.get(name);
    }

    public Profile getCurrentProfile() {
        return profileMap.get(getSysPref("currentProfile", "default"));
    }

    public void setCurrentProfile(Profile profile) {
        if (profile == null) {
            SYSPREFS.remove("currentProfile");
        } else {
            putSysPref("currentProfile", profile.getName());
        }
    }

    public List<Profile> getProfiles() {
        return new ArrayList<>(profileMap.values());
    }

    public MaridBeanConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
