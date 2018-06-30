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
package org.marid.ui.webide.prefs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.ui.webide.base.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import static org.marid.applib.utils.Locales.s;

@Component
@ComponentScan
public class PrefShell extends Shell implements WithImages {

  public PrefShell(UI ui) {
    super(ui.display, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.TITLE);
    setMaximized(true);
    setLayout(new GridLayout(1, false));
    setText(s("preferences"));
    setImage(image(IaIcon.PREFERENCES));
  }

  @Autowired
  public void initContext(GenericApplicationContext context) {
    addDisposeListener(event -> context.close());
  }

  @EventListener
  public void onStart(ContextStartedEvent event) {
    open();
  }
}
