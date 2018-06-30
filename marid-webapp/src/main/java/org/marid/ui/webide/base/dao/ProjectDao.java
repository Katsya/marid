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
package org.marid.ui.webide.base.dao;

import org.marid.applib.dao.ListDao;
import org.marid.applib.model.ProjectItem;
import org.marid.io.MaridFiles;
import org.marid.ui.webide.base.UserDirectories;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.logging.Level.WARNING;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static java.util.stream.StreamSupport.stream;
import static org.marid.logging.Log.log;

@Component
public class ProjectDao implements ListDao<String, ProjectItem> {

  private final Path directory;

  public ProjectDao(UserDirectories userDirectories) {
    this.directory = userDirectories.getProjectsDirectory();
  }

  private String name(Path file) {
    return file.getFileName().toString();
  }

  @Override
  public void save(Collection<? extends ProjectItem> data) {
    try (final var files = Files.newDirectoryStream(directory, Files::isDirectory)) {
      for (final var file : files) {
        final var name = name(file);
        if (data.stream().map(ProjectItem::getId).noneMatch(name::equals)) {
          MaridFiles.delete(file);
        }
      }
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
    for (final var item : data) {
      try {
        Files.createDirectories(directory.resolve(item.getId()));
      } catch (IOException x) {
        log(WARNING, "Unable to create {0}", x, item);
      }
    }
  }

  @Override
  public List<ProjectItem> load() {
    try (final var files = Files.newDirectoryStream(directory, Files::isDirectory)) {
      return stream(files.spliterator(), false).map(e -> new ProjectItem(name(e))).collect(toUnmodifiableList());
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  @Override
  public Set<String> getIds() {
    try (final var files = Files.newDirectoryStream(directory, Files::isDirectory)) {
      return stream(files.spliterator(), false).map(this::name).collect(toUnmodifiableSet());
    } catch (NoSuchFileException x) {
      return Set.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public long getSize(String id) {
    return MaridFiles.size(directory.resolve(id));
  }
}
