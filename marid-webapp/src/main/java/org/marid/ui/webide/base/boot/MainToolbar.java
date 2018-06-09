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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.springframework.stereotype.Component;

import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;

@Component
public class MainToolbar extends ToolBar {

  public MainToolbar(MainMenu mainMenu) {
    super(mainMenu.getShell(), WRAP | SHADOW_OUT | HORIZONTAL);
    setLayoutData(new GridData(FILL_HORIZONTAL));
  }
}
