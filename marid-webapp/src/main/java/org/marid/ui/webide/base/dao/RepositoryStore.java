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

import org.marid.applib.dao.SortedListStore;
import org.marid.applib.model.RepositoryItem;
import org.marid.applib.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RepositoryStore extends SortedListStore<String, RepositoryItem, RepositoryDao> {

  public RepositoryStore(RepositoryDao dao) {
    super(dao, String::compareTo);
  }

  public List<Repository> repositories() {
    final var providers = dao.selectors();
    return list.stream()
        .flatMap(e -> Stream.ofNullable(providers.get(e.getSelector())).map(e::repository))
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableList());
  }
}
