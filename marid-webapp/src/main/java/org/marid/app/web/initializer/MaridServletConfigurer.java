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

import org.marid.app.web.MaridServlet;
import org.marid.spring.annotation.PrototypeScoped;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
@PrototypeScoped
@Order(2)
public class MaridServletConfigurer implements ServletContextConfigurer {

  private final MaridServlet servlet;

  public MaridServletConfigurer(MaridServlet servlet) {
    this.servlet = servlet;
  }

  @Override
  public void start(ServletContext context) {
    final var r = context.addServlet("maridServlet", servlet);
    r.setLoadOnStartup(4);
    r.setAsyncSupported(true);
    r.addMapping("*.marid");
  }

  @Override
  public void stop(ServletContext context) {
  }

  @Override
  public boolean isStopNeeded() {
    return false;
  }
}
