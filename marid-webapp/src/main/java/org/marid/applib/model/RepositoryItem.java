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
package org.marid.applib.model;

import org.jetbrains.annotations.NotNull;
import org.marid.applib.repository.Repository;
import org.marid.applib.repository.RepositoryProvider;
import org.marid.misc.EHT;

import java.util.TreeMap;

public class RepositoryItem extends EHT implements Elem<String> {

  private transient final String id;

  private String selector;
  private TreeMap<String, String> properties = new TreeMap<>();

  public RepositoryItem(String id) {
    this.id = id;
  }

  public String getSelector() {
    return selector;
  }

  public RepositoryItem setSelector(String selector) {
    this.selector = selector;
    return this;
  }

  public TreeMap<String, String> getProperties() {
    return properties;
  }

  @NotNull
  @Override
  public String getId() {
    return id;
  }

  public Repository repository(RepositoryProvider provider) {
    return provider.getRepository(id, properties);
  }
}
