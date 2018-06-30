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

import org.eclipse.swt.widgets.TabItem;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.ui.webide.prefs.repositories.RepositoryTab;
import org.springframework.stereotype.Component;

import static org.eclipse.swt.SWT.NONE;
import static org.marid.applib.utils.Locales.s;

@Component
public class ArtifactTab extends TabItem implements WithImages {

  public ArtifactTab(RepositoryTab repositoryTab) {
    super(repositoryTab.getParent(), NONE);
    setText(s("artifacts"));
    setImage(image(IaIcon.ARTIFACT, 16));
  }
}
