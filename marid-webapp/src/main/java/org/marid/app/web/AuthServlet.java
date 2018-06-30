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

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.decision.AlwaysUseSessionProfileStorageDecision;
import org.pac4j.core.exception.HttpAction;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class AuthServlet extends HttpServlet {

  private final Config config;
  private final DefaultSecurityLogic<Void, J2EContext> securityLogic = new DefaultSecurityLogic<>() {
    @Override
    protected HttpAction unauthorized(J2EContext context, List<Client> currentClients) {
      return HttpAction.redirect(context, "/public/unauthorized.html");
    }
  };

  public AuthServlet(Config config) {
    this.config = config;
    this.securityLogic.setProfileStorageDecision(new AlwaysUseSessionProfileStorageDecision());
  }

  @Override
  protected void doGet(HttpServletRequest q, HttpServletResponse r) {
    final var path = q.getServletPath();
    final var client = path.substring(1);

    final SecurityGrantedAccessAdapter<Void, J2EContext> granted = (ctx, profiles, params) -> {
      r.sendRedirect("/main.marid");
      return null;
    };

    securityLogic.perform(new J2EContext(q, r), config, granted, (code, context) -> null, client, "user", null, false);
  }
}
