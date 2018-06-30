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

import org.marid.spring.annotation.PrototypeScoped;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.decision.AlwaysUseSessionProfileStorageDecision;
import org.pac4j.core.exception.HttpAction;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@PrototypeScoped
public class SecurityFilter implements Filter {

  private final Config config;
  private final DefaultSecurityLogic<Boolean, J2EContext> logic = new DefaultSecurityLogic<>() {
    @Override
    protected HttpAction unauthorized(J2EContext context, List<Client> currentClients) {
      return HttpAction.redirect(context, "/public/unauthorized.html");
    }
  };
  private final SecurityGrantedAccessAdapter<Boolean, J2EContext> authorize = (ctx, profiles, params) -> true;

  public SecurityFilter(Config config) {
    this.config = config;
    this.logic.setProfileStorageDecision(new AlwaysUseSessionProfileStorageDecision());
  }

  @Override
  public void doFilter(ServletRequest q, ServletResponse r, FilterChain c) throws IOException, ServletException {
    final var context = new J2EContext((HttpServletRequest) q, (HttpServletResponse) r);
    final var result = logic.perform(context, config, authorize, (code, ctx) -> false, null, "user", null, false);
    if (result) {
      c.doFilter(q, r);
    }
  }
}
