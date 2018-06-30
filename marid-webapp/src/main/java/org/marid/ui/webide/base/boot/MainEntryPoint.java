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
package org.marid.ui.webide.base.boot;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.marid.app.common.Images;
import org.marid.spring.ContextUtils;
import org.marid.ui.webide.base.UI;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

public class MainEntryPoint implements EntryPoint {

  public static final String CONTEXT_KEY = "applicationContext";
  public static final String USER_IMAGES = "userImages";

  private final GenericApplicationContext parent;

  public MainEntryPoint(GenericApplicationContext parent) {
    this.parent = parent;
  }

  @Override
  public int createUI() {
    final var display = new Display();
    final var shell = new Shell(display, SWT.NO_TRIM);
    shell.setLayout(new GridLayout(1, false));

    shell.setMaximized(true);

    final var child = ContextUtils.context(parent, c -> {
      c.setId("mainUI");
      c.setDisplayName("mainUI");
      c.registerBean(UI.class, () -> new UI(Map.entry(display, shell)));

      display.setData(CONTEXT_KEY, c);
      display.setData(USER_IMAGES, parent.getBean(Images.class));
    });
    child.refresh();
    child.start();

    shell.addDisposeListener(e -> child.close());
    shell.layout();
    shell.open();

    return 0;
  }
}
