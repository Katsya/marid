/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.marid.ui.webide.base.views.repositories;

import com.vaadin.flow.data.provider.ListDataProvider;
import org.marid.applib.repository.Repository;
import org.marid.ui.webide.base.dao.RepositoryDao;
import org.marid.ui.webide.base.model.RepositoryItem;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RepositoryManager {

  private final RepositoryDao dao;
  private final ArrayList<RepositoryItem> repositories = new ArrayList<>();
  private final ListDataProvider<RepositoryItem> dataProvider = new ListDataProvider<>(repositories);

  public RepositoryManager(RepositoryDao dao) {
    this.dao = dao;
  }

  @PostConstruct
  public void init() {
    repositories.addAll(dao.repositories());
  }

  public void add(RepositoryItem repositoryItem) {
    repositories.add(repositoryItem);
    dataProvider.refreshAll();
  }

  public void remove(RepositoryItem repositoryItem) {
    repositories.remove(repositoryItem);
    dataProvider.refreshAll();
  }

  public void save() {
    for (final var repository : repositories) {
      dao.save(repository);
    }
  }

  public ListDataProvider<RepositoryItem> getDataProvider() {
    return dataProvider;
  }

  public boolean isNew(String repositoryName) {
    return repositories.stream().map(RepositoryItem::getName).noneMatch(repositoryName::equals);
  }

  public List<Repository> repositories() {
    final var providers = dao.selectors();
    return repositories.stream()
        .flatMap(e -> Stream.ofNullable(providers.get(e.getSelector())).map(e::repository))
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableList());
  }
}
