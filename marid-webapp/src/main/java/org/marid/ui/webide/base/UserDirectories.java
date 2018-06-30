/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.ui.webide.base;

import org.marid.app.common.Directories;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class UserDirectories {

  private final Path userDirectory;
  private final Path projectsDirectory;
  private final Path repositoriesDirectory;
  private final Path artifactsFile;

  public UserDirectories(Directories directories, CommonProfile profile) throws IOException {
    final String name = Optional.ofNullable(profile.getEmail())
        .filter(s -> !s.isEmpty())
        .orElse(profile.getUsername());


    userDirectory = directories.getUsers().resolve(name);
    projectsDirectory = userDirectory.resolve("projects");
    repositoriesDirectory = userDirectory.resolve("repositories");
    artifactsFile = userDirectory.resolve("artifacts.lst");

    Files.createDirectories(userDirectory);
    Files.createDirectories(projectsDirectory);
    Files.createDirectories(repositoriesDirectory);

    if (!Files.isRegularFile(artifactsFile)) {
      Files.createFile(artifactsFile);
    }
  }

  public Path getUserDirectory() {
    return userDirectory;
  }

  public Path getProjectsDirectory() {
    return projectsDirectory;
  }

  public Path getRepositoriesDirectory() {
    return repositoriesDirectory;
  }

  public Path getArtifactsFile() {
    return artifactsFile;
  }
}
