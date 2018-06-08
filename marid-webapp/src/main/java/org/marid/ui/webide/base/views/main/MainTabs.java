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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.marid.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

@SpringComponent
public class MainTabs extends VerticalLayout {

  private final Map<Tab, Component> tabComponents;
  private final Tabs tabs;

  public MainTabs(@Qualifier("main") List<? extends Supplier<Tab>> tabs) {
    setSizeFull();

    this.tabComponents = tabs.stream()
        .filter(Component.class::isInstance)
        .collect(toMap(Supplier::get, Component.class::cast, (c1, c2) -> c2, () -> new LinkedHashMap<>(tabs.size())));
    this.tabs = new Tabs(tabComponents.keySet().toArray(new Tab[0]));

    add(this.tabs);
    add(this.tabComponents.values().iterator().next());

    this.tabs.addSelectedChangeListener(e -> {
      final var component = tabComponents.get(this.tabs.getSelectedTab());
      if (component != null) {
        remove(getComponentAt(getComponentCount() - 1));
        add(component);
      }
    });
  }
}
