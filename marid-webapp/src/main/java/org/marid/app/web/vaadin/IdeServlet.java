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
package org.marid.app.web.vaadin;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.InvalidRouteConfigurationException;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.shared.communication.PushMode;
import org.marid.ui.ide.Routes;
import org.marid.ui.ide.base.MainUI;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.vaadin.flow.server.Constants.*;
import static com.vaadin.flow.server.VaadinSession.UI_PARAMETER;

@Component
public class IdeServlet extends VaadinServlet {

  private final IdeInstantiator instantiator;

  public IdeServlet(IdeInstantiator instantiator) {
    this.instantiator = instantiator;
  }

  @Override
  protected DeploymentConfiguration createDeploymentConfiguration() {
    final var properties = new Properties();

    properties.setProperty(UI_PARAMETER, MainUI.class.getName());
    properties.setProperty(SERVLET_PARAMETER_HEARTBEAT_INTERVAL, "60");
    properties.setProperty(SERVLET_PARAMETER_PUSH_MODE, PushMode.AUTOMATIC.name());
    properties.setProperty(SERVLET_PARAMETER_PRODUCTION_MODE, "true");
    properties.setProperty(USE_ORIGINAL_FRONTEND_RESOURCES, "true");

    return createDeploymentConfiguration(properties);
  }

  @Override
  protected VaadinServletService createServletService(DeploymentConfiguration configuration) throws ServiceException {
    final var service = new VaadinServletService(this, configuration) {
      @Override
      protected Instantiator createInstantiator() {
        return instantiator;
      }
    };
    service.init();
    try {
      service.getRouter().getRegistry().setNavigationTargets(Routes.allNavigationTargets());
    } catch (InvalidRouteConfigurationException x) {
      throw new ServiceException(x);
    }
    return service;
  }
}
