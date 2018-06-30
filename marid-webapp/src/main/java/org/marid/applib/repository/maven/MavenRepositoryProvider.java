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

import org.marid.applib.repository.RepositoryProvider;

import java.util.Map;
import java.util.Properties;

public class MavenRepositoryProvider implements RepositoryProvider {

  private final Properties properties = new Properties();

  public MavenRepositoryProvider() {
    properties.setProperty("searchUrl", "http://search.maven.org/solrsearch/select");
  }

  @Override
  public MavenRepository getRepository(String name, Map<String, String> properties) {
    final Properties props = new Properties(this.properties);
    properties.forEach(props::setProperty);
    return new MavenRepository(name, props);
  }

  @Override
  public Properties getProperties() {
    return properties;
  }

  @Override
  public String getName() {
    return "Maven";
  }

  @Override
  public String getDescription() {
    return "Maven Central Repository";
  }
}
