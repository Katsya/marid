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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.marid.spring.init.Init;
import org.springframework.stereotype.Component;

@Route("")
@Component
public class MainComponent extends VerticalLayout {

  public MainComponent() {
    add(new TextField("xxx", "xxx"));
  }

  @Init
  public void addButton() {
    final var button = new Button("XXX");
    button.addClickListener(e -> {
      add(new Button("X", ex -> remove(ex.getSource())));
    });
    add(button);
  }
}
