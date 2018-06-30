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
package org.marid.applib.controls.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import static org.eclipse.swt.SWT.DROP_DOWN;
import static org.eclipse.swt.SWT.POP_UP;

public class DropDownToolItem extends ToolItem {

  public final Menu menu;

  public DropDownToolItem(ToolBar toolBar, int index) {
    super(toolBar, DROP_DOWN, index);
    menu = new Menu(toolBar.getShell(), POP_UP);
    addListener(SWT.Selection, e -> {
      final var r = getBounds();
      final var p = toolBar.toDisplay(r.x, r.y + r.height);
      menu.setLocation(p);
      menu.setVisible(true);
    });
  }

  public DropDownToolItem(ToolBar toolBar) {
    this(toolBar, toolBar.getItemCount());
  }
}
