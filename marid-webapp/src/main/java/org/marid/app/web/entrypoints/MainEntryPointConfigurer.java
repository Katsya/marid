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
package org.marid.app.web.entrypoints;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.client.WebClient;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.ui.webide.base.boot.MainEntryPoint;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@PrototypeScoped
public class MainEntryPointConfigurer implements EntryPointConfigurer {

  @Override
  public void configure(Application application, GenericApplicationContext context) {
    final var classLoader = Thread.currentThread().getContextClassLoader();
    final var params = Map.of(
        WebClient.PAGE_TITLE, "Marid IDE",
        WebClient.FAVICON, "favicon.png"
    );

    application.addResource("favicon.png", resourceName -> classLoader.getResourceAsStream("META-INF/resources/public/marid32.png"));
    application.addEntryPoint("/main.marid", () -> new MainEntryPoint(context), params);
  }
}
