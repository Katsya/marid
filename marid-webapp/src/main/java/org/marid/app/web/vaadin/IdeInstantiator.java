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

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.marid.spring.ContextUtils;
import org.marid.ui.ide.base.BaseConfiguration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;

@Component
public class IdeInstantiator implements Instantiator {

  private final GenericApplicationContext parent;
  private final IdeI18nProvider ideI18nProvider;
  private final ConcurrentHashMap<UI, GenericApplicationContext> contexts = new ConcurrentHashMap<>();

  public IdeInstantiator(GenericApplicationContext parent, IdeI18nProvider ideI18nProvider) {
    this.parent = parent;
    this.ideI18nProvider = ideI18nProvider;
  }

  @Override
  public boolean init(VaadinService service) {
    return true;
  }

  @Override
  public Stream<VaadinServiceInitListener> getServiceInitListeners() {
    return parent.getBeansOfType(VaadinServiceInitListener.class).values().stream();
  }

  private <T> T get(Class<T> type, UI ui, NavigationEvent event) {
    if (ui == null) {
      return parent.getBean(type);
    } else {
      return contexts.computeIfAbsent(ui, u -> ContextUtils.context(parent, c -> {
        c.setId(type.getName());
        c.setDisplayName(type.getName());
        u.addDetachListener(e -> close(u));
        c.registerBean(BaseConfiguration.class, () -> new BaseConfiguration(u, event));
        c.refresh();
        c.start();
      })).getBean(type);
    }
  }

  @Override
  public <T> T getOrCreate(Class<T> type) {
    return get(type, UI.getCurrent(), null);
  }

  @Override
  public <T extends HasElement> T createRouteTarget(Class<T> routeTargetType, NavigationEvent event) {
    return get(routeTargetType, event.getUI(), event);
  }

  @Override
  public I18NProvider getI18NProvider() {
    return ideI18nProvider;
  }

  @Override
  public Stream<BootstrapListener> getBootstrapListeners(Stream<BootstrapListener> serviceInitListeners) {
    return Stream.concat(
        serviceInitListeners,
        parent.getBeansOfType(BootstrapListener.class).values().stream()
    );
  }

  public void close(UI ui) {
    try (final var context = contexts.remove(ui)) {
      if (context != null) {
        log(FINE, "Closing {0}", context);
      }
    } catch (Exception x) {
      log(WARNING, "Unable to close ui context {0}", x, ui.getUIId());
    }
  }
}
