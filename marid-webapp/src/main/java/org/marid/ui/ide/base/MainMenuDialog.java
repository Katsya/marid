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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.marid.idelib.MaridIcon;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.init.Init;
import org.springframework.stereotype.Component;

import static org.marid.ui.ide.I18N.s;

@Component
@PrototypeScoped
public class MainMenuDialog extends Dialog {

  private final VerticalLayout layout = new VerticalLayout();

  public MainMenuDialog() {
    layout.setAlignItems(FlexComponent.Alignment.STRETCH);

    add(layout);
  }

  @Init
  public void addCloseSessionButton() {
    final var button = new Button(s("closeSession"), MaridIcon.SESSION_CLOSE.newIcon());
    button.addClickListener(e -> {
      final var page = UI.getCurrent().getPage();
      page.executeJavaScript("window.location.replace('/logout')");
      VaadinSession.getCurrent().close();
    });
    layout.add(button);
  }

  @Init
  public void addCloseButton() {
    final var button = new Button(s("close"), MaridIcon.CLOSE.newIcon());
    button.addClickListener(e -> close());
    layout.add(button);
  }
}
