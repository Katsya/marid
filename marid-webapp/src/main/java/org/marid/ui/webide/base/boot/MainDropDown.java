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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.controls.toolbar.DropDownToolItem;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.applib.utils.Locales;
import org.marid.spring.init.Init;
import org.springframework.stereotype.Component;

@Component
public class MainDropDown extends DropDownToolItem implements WithImages {

  public MainDropDown(MainMenu mainMenu) {
    super(mainMenu, 0);
    setImage(maridIcon(24));
    new ToolItem(mainMenu, SWT.SEPARATOR, 1);
  }

  @Init
  public void closeSessionItem() {
    final MenuItem item = new MenuItem(menu, SWT.PUSH);
    item.setText(Locales.s("closeSession"));
    item.setImage(image(IaIcon.CANCEL, 16));
    item.addListener(SWT.Selection, e -> {
      final var jsExecutor = RWT.getClient().getService(JavaScriptExecutor.class);
      jsExecutor.execute("window.location.replace('/logout')");
    });
  }
}
