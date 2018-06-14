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
package org.marid.applib.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class Pane extends Composite {

  protected final ToolBar toolbar;

  public Pane(Composite parent, int style, int toolbarStyle) {
    super(parent, style);
    setLayout(new GridLayout(1, false));
    toolbar = new ToolBar(this, toolbarStyle);
    toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  protected void addSeparator() {
    new ToolItem(toolbar, SWT.SEPARATOR);
  }
}
