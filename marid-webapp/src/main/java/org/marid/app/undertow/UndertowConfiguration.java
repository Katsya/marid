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

package org.marid.app.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.CanonicalPathHandler;
import io.undertow.server.handlers.RedirectHandler;
import org.marid.app.props.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;

import static io.undertow.UndertowOptions.*;

@Component
public class UndertowConfiguration {

  @Bean
  public HttpHandler servletHandler(DeploymentManagerProvider deploymentManagerProvider) throws ServletException {
    return deploymentManagerProvider.start();
  }

  @Bean
  public HttpHandler rootHandler(HttpHandler servletHandler) {
    return new CanonicalPathHandler(exchange -> {
      switch (exchange.getRelativePath()) {
        case "/":
          new RedirectHandler("/main.marid").handleRequest(exchange);
          break;
        default:
          servletHandler.handleRequest(exchange);
          break;
      }
    });
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Undertow undertow(SSLContext sslContext, WebProperties properties, HttpHandler rootHandler) {
    return Undertow.builder()
        .setIoThreads(4)
        .setWorkerThreads(8)
        .setServerOption(ALWAYS_SET_KEEP_ALIVE, true)
        .setServerOption(ALWAYS_SET_DATE, true)
        .setServerOption(ENABLE_HTTP2, true)
        .setServerOption(HTTP2_SETTINGS_ENABLE_PUSH, true)
        .addListener(new Undertow.ListenerBuilder()
            .setType(Undertow.ListenerType.HTTPS)
            .setHost(properties.getHost())
            .setPort(properties.getPort())
            .setRootHandler(rootHandler)
            .setSslContext(sslContext)
        )
        .build();
  }
}
