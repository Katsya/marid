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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.spring.ContextUtils;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.UI;
import org.marid.ui.webide.prefs.PrefShell;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;

@Component
public class MainMenu extends ToolBar implements WithImages {

  public MainMenu(UI ui) {
    super(ui.shell, BORDER | WRAP | SHADOW_OUT);
    setLayoutData(new GridData(FILL_HORIZONTAL));
  }

  @Init
  public void prefItem(GenericApplicationContext parent) {
    final ToolItem item = new ToolItem(this, SWT.PUSH);
    item.setImage(image(IaIcon.PREFERENCES));
    item.addListener(Selection, e -> ContextUtils.context(parent, c -> {
      c.register(PrefShell.class);
      c.refresh();
      c.start();
    }));
  }
}
