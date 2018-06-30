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
package org.marid.app.web;

import org.marid.app.web.initializer.ServletContextConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.LinkedList;
import java.util.List;

import static java.util.logging.Level.INFO;
import static org.marid.logging.Log.log;

@Component
public class MaridListener implements ServletContextListener {

  private final ObjectProvider<List<? extends ServletContextConfigurer>> configurersProvider;
  private final List<ServletContextConfigurer> configurers = new LinkedList<>();

  public MaridListener(ObjectProvider<List<? extends ServletContextConfigurer>> configurersProvider) {
    this.configurersProvider = configurersProvider;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    for (final var configurer : configurersProvider.getObject()) {
      log(INFO, "Configuring {0}", configurer.getClass().getName());
      configurer.start(sce.getServletContext());
      if (configurer.isStopNeeded()) {
        configurers.add(configurer);
      }
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    for (final var configurer : configurers) {
      log(INFO, "Stopping {0}", configurer.getClass().getName());
      configurer.stop(sce.getServletContext());
    }
  }
}
