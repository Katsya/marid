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
package org.marid.ui.webide.base.projects;

import org.eclipse.swt.custom.CTabItem;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.applib.utils.Locales;
import org.marid.ui.webide.base.boot.MainTabs;
import org.springframework.stereotype.Component;

import static org.eclipse.swt.SWT.NONE;

@Component
public class ProjectTab extends CTabItem implements WithImages {

  public ProjectTab(MainTabs mainTabs) {
    super(mainTabs, NONE);

    mainTabs.setSelection(this);
    setShowClose(false);
    setText(Locales.s("projects"));
    setImage(image(IaIcon.PROJECT, 16));
  }
}
