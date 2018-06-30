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
package org.marid.app.web.dyn;

import io.undertow.util.Headers;
import org.marid.image.MaridIcon;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Optional;

@Component
public class GetHandlers {

  @DynResource(name = "/icon.gif")
  public GetHandler maridIconHandler() {
    return (q, r) -> {
      final int size = Optional.ofNullable(q.getParameter("size")).map(Integer::valueOf).orElse(16);
      final Color color = Optional.ofNullable(q.getParameter("color")).map(Color::getColor).orElse(Color.GREEN);

      r.setStatus(HttpServletResponse.SC_OK);
      r.setHeader(Headers.CONTENT_TYPE_STRING, "image/gif");

      final var image = MaridIcon.getImage(size, color);
      ImageIO.write(image, "gif", r.getOutputStream());
    };
  }
}
