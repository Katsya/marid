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
package org.marid.app.web.initializer;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.marid.app.common.Directories;
import org.marid.app.web.entrypoints.EntryPointConfigurer;
import org.marid.spring.annotation.PrototypeScoped;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.List;

@Component
@Order(1)
@PrototypeScoped
public class ApplicationConfigurer implements ServletContextConfigurer, ApplicationConfiguration {

  private final GenericApplicationContext context;
  private final String directory;
  private final ObjectProvider<List<? extends EntryPointConfigurer>> entryPointConfigurers;

  private ApplicationRunner runner;

  public ApplicationConfigurer(GenericApplicationContext context,
                               Directories directories,
                               ObjectProvider<List<? extends EntryPointConfigurer>> entryPointConfigurers) {
    this.context = context;
    this.directory = directories.getRwtDir().toString();
    this.entryPointConfigurers = entryPointConfigurers;
  }

  @Override
  public void start(ServletContext context) {
    context.setAttribute(ApplicationConfiguration.RESOURCE_ROOT_LOCATION, directory);
    runner = new ApplicationRunner(this, context);
    runner.start();
  }

  @Override
  public void stop(ServletContext context) {
    try {
      runner.stop();
    } finally {
      runner = null;
    }
  }

  @Override
  public boolean isStopNeeded() {
    return true;
  }

  @Override
  public void configure(Application application) {
    for (final var configurer : entryPointConfigurers.getObject()) {
      configurer.configure(application, context);
    }
  }
}
