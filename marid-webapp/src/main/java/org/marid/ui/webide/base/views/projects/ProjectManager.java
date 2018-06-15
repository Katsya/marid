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
package org.marid.ui.webide.base.views.projects;

import org.marid.applib.dao.SortedListManager;
import org.marid.applib.model.ProjectItem;
import org.marid.ui.webide.base.dao.ProjectDao;
import org.springframework.stereotype.Component;

@Component
public class ProjectManager extends SortedListManager<String, ProjectItem, ProjectDao> {

  public ProjectManager(ProjectDao dao) {
    super(dao);
  }

  public long getSize(String id) {
    return dao.getSize(id);
  }
}
