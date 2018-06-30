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

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CallbackServlet extends HttpServlet {

  private final Config config;
  private final DefaultCallbackLogic<Void, J2EContext> callbackLogic;

  public CallbackServlet(Config config) {
    this.config = config;
    this.callbackLogic = new DefaultCallbackLogic<>();
  }

  @Override
  protected void doGet(HttpServletRequest q, HttpServletResponse r) {
    callbackLogic.perform(new J2EContext(q, r), config, (code, ctx) -> null, "/main.marid", true, false, null, null);
  }
}
