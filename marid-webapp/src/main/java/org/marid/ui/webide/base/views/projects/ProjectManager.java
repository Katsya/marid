/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.marid.ui.webide.base.views.projects;

import com.vaadin.flow.data.provider.ListDataProvider;
import org.marid.ui.webide.base.dao.ProjectDao;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class ProjectManager {

  private final ProjectDao dao;
  private final ArrayList<String> projects = new ArrayList<>();
  private final ListDataProvider<String> dataProvider = new ListDataProvider<>(projects);

  public ProjectManager(ProjectDao dao) {
    this.dao = dao;
  }

  @PostConstruct
  public void refresh() {
    projects.clear();
    projects.addAll(dao.getProjectNames());
    dataProvider.refreshAll();
  }

  public void refresh(String project) {
    dataProvider.refreshItem(project);
  }

  public void remove(Collection<String> projects) {
    projects.forEach(dao::removeProject);
    refresh();
  }

  public void add(String project) {
    dao.tryCreate(project);
    refresh();
  }

  public ListDataProvider<String> getDataProvider() {
    return dataProvider;
  }
}
