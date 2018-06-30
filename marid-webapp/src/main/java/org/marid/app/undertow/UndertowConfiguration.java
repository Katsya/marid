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
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import org.marid.app.common.Directories;
import org.marid.app.props.WebProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;

import static io.undertow.UndertowOptions.*;
import static org.xnio.Options.KEEP_ALIVE;

@Component
public class UndertowConfiguration {

  @Bean
  public HttpHandler servletHandler(DeploymentManagerProvider deploymentManagerProvider) throws ServletException {
    return deploymentManagerProvider.start();
  }

  @Bean
  public HttpHandler rootHandler(HttpHandler servletHandler) {
    final var redirect = new RedirectHandler("/main.marid");
    return new CanonicalPathHandler(e -> {
      switch (e.getRelativePath()) {
        case "/":
          redirect.handleRequest(e);
          break;
        default:
          servletHandler.handleRequest(e);
          break;
      }
    });
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Undertow undertow(SSLContext sslContext, WebProperties properties, HttpHandler rootHandler) {
    return Undertow.builder()
        .setIoThreads(4)
        .setWorkerThreads(8)
        .setBufferSize(2048)
        .setDirectBuffers(false)
        .setServerOption(ALWAYS_SET_KEEP_ALIVE, true)
        .setServerOption(ALWAYS_SET_DATE, true)
        .setServerOption(ENABLE_HTTP2, true)
        .setServerOption(HTTP2_SETTINGS_ENABLE_PUSH, true)
        .setServerOption(ENABLE_RFC6265_COOKIE_VALIDATION, true)
        .setServerOption(NO_REQUEST_TIMEOUT, 600_000)
        .setSocketOption(KEEP_ALIVE, true)
        .setHandler(rootHandler)
        .addListener(new Undertow.ListenerBuilder()
            .setType(Undertow.ListenerType.HTTPS)
            .setHost(properties.getHost())
            .setPort(properties.getPort())
            .setSslContext(sslContext)
        )
        .build();
  }

  @Bean
  @Qualifier("resourceManager")
  @Order(1)
  public ClassPathResourceManager metaInfResources() {
    return new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "META-INF/resources");
  }

  @Bean
  @Qualifier("resourceManager")
  @Order(2)
  public PathResourceManager rwtResources(Directories directories) {
    return new PathResourceManager(directories.getRwtDir(), 1024, true, false, false);
  }
}
