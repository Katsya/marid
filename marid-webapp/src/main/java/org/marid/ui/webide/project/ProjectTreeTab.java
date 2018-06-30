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
package org.marid.ui.webide.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.applib.model.ProjectItem;
import org.marid.ui.webide.base.boot.MainTabs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProjectTreeTab extends CTabItem implements WithImages {

  public ProjectTreeTab(MainTabs mainTabs, ProjectItem item) {
    super(mainTabs, SWT.CLOSE);
    setText(item.getId());
    setImage(image(IaIcon.PROJECT, 16));
    setData(item);
  }

  @Autowired
  public void initControl(ProjectTree tree) {
    setControl(tree);
  }

  @Autowired
  public void initClose(GenericApplicationContext context) {
    addListener(SWT.Dispose, e -> context.close());
  }
}
