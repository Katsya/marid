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
package org.marid.ui.webide.base.projects;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.marid.applib.dialogs.ShellDialog;
import org.marid.applib.image.AppIcon;
import org.marid.applib.image.ToolIcon;
import org.marid.applib.model.ProjectItem;
import org.marid.misc.ListenableValue;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.UI;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.eclipse.swt.SWT.BORDER;
import static org.eclipse.swt.SWT.SINGLE;
import static org.marid.applib.utils.Locales.m;
import static org.marid.applib.utils.Locales.s;
import static org.marid.applib.validators.InputValidators.*;

@Component
@PrototypeScoped
public class ProjectAddDialog extends ShellDialog {

  private final IInputValidator validator;
  private final ListenableValue<String> valid = new ListenableValue<>();
  private final ListenableValue<String> name = new ListenableValue<>("project");

  public ProjectAddDialog(UI ui, ProjectStore store) {
    super(ui.shell);
    validator = inputs(fileName(), input(o -> o.filter(store::contains).map(id -> m("duplicateItem", id))));
    setImage(image(AppIcon.PROJECT));
    setText(s("addProject"));
  }

  @Init
  public void nameField() {
    final var field = addField(s("name"), ToolIcon.PROJECT, c -> new Text(c, BORDER | SINGLE));
    field.setText(name.get());
    field.addModifyListener(e -> {
      valid.accept(validator.isValid(field.getText()));
      name.accept(field.getText());
    });
    ((GridData) field.getLayoutData()).minimumWidth = 100;
    bindValidation(valid, field);
  }

  @Init
  public void cancelButton() {
    addButton(s("cancel"), ToolIcon.CANCEL, e -> close());
  }

  @Init
  public void addButton(ProjectStore store) {
    final var button = addButton(s("add"), ToolIcon.ADD, e -> store.add(List.of(new ProjectItem(name.get()))));
    valid.addListener((o, n) -> button.setEnabled(n == null));
  }
}
