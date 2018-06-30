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
package org.marid.idelib;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.LinkedHashMap;
import java.util.Optional;

public class TabPane extends VerticalLayout {

  private final Tabs tabs = new Tabs();
  private final LinkedHashMap<Tab, Component> components = new LinkedHashMap<>();

  public TabPane() {
    setPadding(false);
    setAlignItems(Alignment.STRETCH);
    add(tabs);
    tabs.addSelectedChangeListener(e -> {
      final var component = components.get(tabs.getSelectedTab());
      if (getComponentCount() > 1) {
        replace(getComponentAt(1), component);
      } else {
        add(component);
      }
      setFlexGrow(1d, component);
    });
  }

  public void addTab(Tab tab, Component component) {
    components.put(tab, component);
    tabs.add(tab);
    tab.addDetachListener(e -> components.remove(tab));
  }

  public void addTab(MaridIcon icon, String label, Component component) {
    final var tab = new Tab();
    tab.add(icon.newIcon(), new Span(label));
    addTab(tab, component);
  }

  public Tabs getTabs() {
    return tabs;
  }

  public Optional<Component> getCurrentComponent() {
    return getChildren().skip(1L).findFirst();
  }
}
