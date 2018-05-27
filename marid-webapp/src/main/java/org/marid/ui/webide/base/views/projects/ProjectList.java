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

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Grid;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.misc.StringUtils;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.dao.ProjectsDao;

import java.util.Locale;

@SpringComponent
public class ProjectList extends Grid<String> implements Inits {

  public ProjectList(ProjectManager model) {
    super(model.getDataProvider());
    setSizeFull();
  }

  @Init
  public void initNameColumn(Strs strs) {
    addColumn(ValueProvider.identity())
        .setCaption(strs.s("name"))
        .setId("name")
        .setExpandRatio(4);
  }

  @Init
  public void initSizeColumn(Strs strs, Locale locale, ProjectsDao dao) {
    addColumn(project -> StringUtils.sizeBinary(locale, dao.getSize(project), 2))
        .setCaption(strs.s("size"))
        .setId("size")
        .setExpandRatio(1)
        .setMinimumWidthFromContent(true);
  }
}
