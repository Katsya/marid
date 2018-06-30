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
package org.marid.applib.repository.maven;

import org.marid.applib.repository.ArtifactFinder;
import org.marid.applib.repository.Repository;

import java.net.URI;
import java.util.Properties;

public class MavenRepository implements Repository {

  private final String name;
  private final Properties properties;

  public MavenRepository(String name, Properties properties) {
    this.name = name;
    this.properties = properties;
  }

  @Override
  public ArtifactFinder getArtifactFinder() {
    final String searchUrl = properties.getProperty("searchUrl");
    return new MavenArtifactFinder(URI.create(searchUrl));
  }

  @Override
  public String getName() {
    return name;
  }
}
