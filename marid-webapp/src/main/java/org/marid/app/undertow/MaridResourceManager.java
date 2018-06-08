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
package org.marid.app.undertow;

import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MaridResourceManager implements ResourceManager {

  private final Logger logger;

  public MaridResourceManager(Logger logger) {
    this.logger = logger;
  }

  @Override
  public Resource getResource(String path) {
    final var classLoader = Thread.currentThread().getContextClassLoader();
    final var url = classLoader.getResource("META-INF/resources" + path);

    if (url == null) {
      logger.warn("No resource found: {}", path);
      return null;
    } else {
      return new URLResource(url, path);
    }
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return false;
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
  }

  @Override
  public void close() {
  }
}
