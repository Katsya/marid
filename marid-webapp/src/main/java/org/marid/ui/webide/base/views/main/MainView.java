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
package org.marid.ui.webide.base.views.main;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.marid.applib.spring.init.Init;
import org.marid.spring.annotation.SpringComponent;

@Route
@SpringComponent
public class MainView extends VerticalLayout implements RouterLayout {

  public MainView() {
    setSizeFull();
  }

  @Init
  public void initToolbar(MainToolbar toolbar) {
    add(toolbar);
    setFlexGrow(0, toolbar);
  }

  @Init
  public void initTabs(MainTabs tabs) {
    add(tabs);
    setFlexGrow(1, tabs);
  }
}
