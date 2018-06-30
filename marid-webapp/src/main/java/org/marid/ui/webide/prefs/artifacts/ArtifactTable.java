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
package org.marid.ui.webide.prefs.artifacts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.controls.pane.TablePane;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.applib.utils.Tables;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.dao.ArtifactStore;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import static org.marid.applib.image.IaIcon.*;

@Component
public class ArtifactTable extends TablePane implements WithImages {

  public ArtifactTable(ArtifactTab tab) {
    super(tab.getParent());
    tab.setControl(this);
    addColumn("groupId", 100);
    addColumn("artifactId", 100);
    addColumn("version", 75);
  }

  @Init
  public void init(ArtifactStore store) {
    Tables.init(this, store, (item, e) -> {
      item.setText(new String[] {e.getGroupId(), e.getArtifactId(), e.getVersion()});
      item.setImage(new Image[] {image(GROUP, 16), image(ARTIFACT, 16), image(SELECTOR, 16)});
    });
  }

  @Init
  public void findButton(ObjectFactory<ArtifactFindDialog> dialogFactory) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.FIND));
    item.addListener(SWT.Selection, e -> dialogFactory.getObject().open());
  }

  @Init
  public void stdButtons(ArtifactStore store) {
    Tables.addStdButtons(this, store);
  }
}
