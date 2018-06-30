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
package org.marid.ui.webide.base;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UIPinger implements Runnable {

  private final UI ui;

  public UIPinger(UI ui) {
    this.ui = ui;
  }

  @PostConstruct
  public void init() {
    ui.display.timerExec(30_000, this);
  }

  @Override
  public void run() {
    init();
    ui.display.wake();
  }
}
