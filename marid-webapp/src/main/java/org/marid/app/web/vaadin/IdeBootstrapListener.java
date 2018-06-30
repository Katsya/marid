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

import com.vaadin.flow.server.BootstrapListener;
import com.vaadin.flow.server.BootstrapPageResponse;
import org.springframework.stereotype.Component;

@Component
public class IdeBootstrapListener implements BootstrapListener {

  @Override
  public void modifyBootstrapPage(BootstrapPageResponse response) {
    final var document = response.getDocument();
    final var head = document.head();

    head.appendChild(document.createElement("link")
        .attr("rel", "icon")
        .attr("type", "image/gif")
        .attr("href", "/dyn/icon.gif?size=24")
    );
  }
}
