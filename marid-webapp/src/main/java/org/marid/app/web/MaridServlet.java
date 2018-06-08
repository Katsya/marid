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
package org.marid.app.web;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.server.*;
import org.marid.app.common.UIContexts;
import org.marid.spring.annotation.MaridComponent;
import org.marid.ui.webide.base.views.main.MainView;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@MaridComponent
public class MaridServlet extends VaadinServlet {

  private final GenericApplicationContext context;

  public MaridServlet(GenericApplicationContext context) {
    this.context = context;
  }

  @Override
  protected VaadinServletService createServletService(DeploymentConfiguration configuration) throws ServiceException {
    final var service = new Service(this, configuration);
    service.init();
    return service;
  }

  public GenericApplicationContext getContext() {
    return context;
  }

  private static final class Service extends VaadinServletService implements Instantiator {

    private Service(MaridServlet servlet, DeploymentConfiguration deploymentConfiguration) {
      super(servlet, deploymentConfiguration);
      setSystemMessagesProvider(systemMessagesInfo -> {
        final var messages = new CustomizedSystemMessages();
        messages.setSessionExpiredNotificationEnabled(false);
        return messages;
      });
    }

    @Override
    protected Optional<Instantiator> loadInstantiators() {
      return Optional.of(this);
    }

    @Override
    public void init() throws ServiceException {
      try {
        getRouteRegistry().setNavigationTargets(Set.of(MainView.class));
      } catch (InvalidRouteConfigurationException x) {
        throw new ServiceException(x);
      }
      super.init();
    }

    @Override
    public boolean init(VaadinService service) {
      return service == this;
    }

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
      return DefaultInstantiator.getServiceLoaderListeners(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public <T extends HasElement> T createRouteTarget(Class<T> routeTargetType, NavigationEvent event) {
      return getOrCreate(routeTargetType, event.getUI());
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
      return getOrCreate(type, UI.getCurrent());
    }

    private <T> T getOrCreate(Class<T> type, UI ui) {
      final var context = ((MaridServlet) getServlet()).context;
      if (ui == null) {
        return context.getBean(type);
      } else {
        final var uiContexts = context.getBean(UIContexts.class);
        final var uiContext = uiContexts.getContextFor(ui);
        return uiContext != null ? uiContext.getBean(type) : context.getBean(type);
      }
    }
  }
}
