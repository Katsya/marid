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

import org.marid.app.web.dyn.DynResource;
import org.marid.app.web.dyn.GetHandler;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class DynServlet extends HttpServlet {

  private final Map<String, GetHandler> getHandlers;

  public DynServlet(GenericApplicationContext context) {
    final var map = new LinkedHashMap<String, GetHandler>();
    for (final var beanName : context.getBeanNamesForType(GetHandler.class)) {
      final var definition = (AnnotatedBeanDefinition) context.getBeanDefinition(beanName);
      final var metadata = Objects.requireNonNull(definition.getFactoryMethodMetadata());
      final var attributes = Objects.requireNonNull(metadata.getAnnotationAttributes(DynResource.class.getName()));
      map.put(attributes.get("name").toString(), context.getBean(beanName, GetHandler.class));
    }
    getHandlers = Map.copyOf(map);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    final var handler = getHandlers.get(req.getPathInfo());
    if (handler == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    } else {
      handler.handle(req, resp);
    }
  }
}
