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
package org.marid.ui.ide.base;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.NavigationEvent;
import com.vaadin.flow.server.VaadinSession;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@ComponentScan
public class BaseConfiguration {

  private final UI ui;
  private final NavigationEvent navigationEvent;

  @Autowired(required = false)
  public BaseConfiguration(UI ui, NavigationEvent navigationEvent) {
    this.ui = ui;
    this.navigationEvent = navigationEvent;
  }

  @Bean(destroyMethod = "")
  public UI ui() {
    return ui;
  }

  @Bean
  public NavigationEvent navigationEvent() {
    return navigationEvent;
  }

  @Bean
  public CommonProfile userProfile() {
    final var session = VaadinSession.getCurrent().getSession();
    final var map = (LinkedHashMap) session.getAttribute(Pac4jConstants.USER_PROFILES);
    return (CommonProfile) map.values().iterator().next();
  }
}
