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
import org.marid.applib.model.RepositoryItem;
import org.marid.applib.repository.RepositoryProvider;
import org.marid.collections.MaridIterators;
import org.marid.ui.webide.base.UserDirectories;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;
import static org.marid.applib.json.MaridJackson.MAPPER;

@Component
public class RepositoryDao implements ListDao<String, RepositoryItem> {

  private final Path directory;

  public RepositoryDao(UserDirectories directories) {
    directory = directories.getRepositoriesDirectory();
  }

  private String name(Path file) {
    return StringUtils.stripFilenameExtension(file.getFileName().toString());
  }

  @Override
  public void save(Collection<? extends RepositoryItem> data) {
    try (final var files = Files.newDirectoryStream(directory, "*.repo")) {
      for (final var file : files) {
        final var name = name(file);
        if (data.stream().map(RepositoryItem::getId).noneMatch(name::equals)) {
          Files.deleteIfExists(file);
        }
      }
    } catch (NoSuchFileException x) {
      // nop
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
    for (final var item : data) {
      final var path = directory.resolve(item.getId() + ".repo");
      try (final var writer = Files.newBufferedWriter(path, UTF_8)) {
        MAPPER.writeValue(writer, item);
      } catch (IOException x) {
        throw new UncheckedIOException(x);
      }
    }
  }

  @Override
  public List<RepositoryItem> load() {
    try (final var files = Files.newDirectoryStream(directory, "*.repo")) {
      final var list = new LinkedList<RepositoryItem>();
      for (final var file : files) {
        final var item = new RepositoryItem(StringUtils.stripFilenameExtension(file.getFileName().toString()));
        final var objectReader = MAPPER.readerForUpdating(item);
        try (final var reader = Files.newBufferedReader(file, UTF_8)) {
          objectReader.readValue(reader);
        } catch (NoSuchFileException x) {
          continue;
        } catch (IOException x) {
          throw new UncheckedIOException(x);
        }
        list.add(item);
      }
      return List.copyOf(list);
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  @Override
  public Set<String> getIds() {
    try (final var files = Files.newDirectoryStream(directory, "*.repo")) {
      return MaridIterators.stream(files)
          .map(Path::getFileName)
          .map(Path::toString)
          .map(StringUtils::stripFilenameExtension)
          .collect(Collectors.toUnmodifiableSet());
    } catch (NoSuchFileException x) {
      return Set.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public TreeMap<String, RepositoryProvider> selectors() {
    return ServiceLoader.load(RepositoryProvider.class).stream()
        .map(Provider::get)
        .collect(toMap(RepositoryProvider::getName, p -> p, (v1, v2) -> v2, TreeMap::new));
  }
}
