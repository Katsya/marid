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
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.marid.spring.init.Init;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
public class MainMenu extends HorizontalLayout {

  @Init
  public void addMainButton(ObjectFactory<MainMenuDialog> mainMenuDialogFactory) {
    final var button = new Button(new Image("/dyn/icon.gif?size=24", "Marid"));
    button.setAutofocus(false);
    button.addClickListener(e -> mainMenuDialogFactory.getObject().open());
    add(button);
  }
}
