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
package org.marid.idelib;

public enum MaridIcon {

  CLOSE("/svg/cross.svg"),
  SESSION_CLOSE("/svg/sessionClose.svg"),
  PROJECT("/svg/project.svg")
  ;

  private final String url;

  MaridIcon(String url) {
    this.url = url;
  }

  public IconImage newIcon() {
    return newIcon("4px");
  }

  public IconImage newIcon(String margin) {
    final var image = new IconImage(url);
    image.getStyle().set("margin-right", margin);
    return image;
  }
}
