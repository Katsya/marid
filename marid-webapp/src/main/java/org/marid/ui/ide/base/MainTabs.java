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
package org.marid.ui.ide.base;

import org.marid.idelib.MaridIcon;
import org.marid.idelib.TabPane;
import org.marid.spring.init.Init;
import org.marid.ui.ide.I18N;
import org.marid.ui.ide.base.projects.ProjectTable;
import org.springframework.stereotype.Component;

@Component
public class MainTabs extends TabPane {

  @Init
  public void addProject(ProjectTable projectTable) {
    addTab(MaridIcon.PROJECT, I18N.s("projects"), projectTable);
  }
}
