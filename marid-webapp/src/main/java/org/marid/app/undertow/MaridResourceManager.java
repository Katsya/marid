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
package org.marid.app.undertow;

import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Stream;

@Component
public class MaridResourceManager implements ResourceManager {

  private final ResourceManager[] resourceManagers;

  public MaridResourceManager(@Qualifier("resourceManager") ResourceManager[] resourceManagers) {
    this.resourceManagers = resourceManagers;
  }

  @Override
  public Resource getResource(String path) throws IOException {
    for (final var resourceManager : resourceManagers) {
      final var resource = resourceManager.getResource(path);
      if (resource != null) {
        return resource;
      }
    }
    return null;
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return Stream.of(resourceManagers).anyMatch(ResourceManager::isResourceChangeListenerSupported);
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
    Stream.of(resourceManagers)
        .filter(ResourceManager::isResourceChangeListenerSupported)
        .forEach(m -> m.registerResourceChangeListener(listener));
  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
    Stream.of(resourceManagers)
        .filter(ResourceManager::isResourceChangeListenerSupported)
        .forEach(m -> m.removeResourceChangeListener(listener));
  }

  @Override
  public void close() {
  }
}
