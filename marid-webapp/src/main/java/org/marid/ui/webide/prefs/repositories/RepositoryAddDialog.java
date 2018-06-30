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
package org.marid.ui.webide.prefs.repositories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.marid.applib.dialogs.ShellDialog;
import org.marid.applib.image.IaIcon;
import org.marid.applib.model.RepositoryItem;
import org.marid.misc.ListenableValue;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.dao.RepositoryDao;
import org.marid.ui.webide.base.dao.RepositoryStore;
import org.marid.ui.webide.prefs.PrefShell;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.eclipse.swt.SWT.*;
import static org.marid.applib.utils.Locales.m;
import static org.marid.applib.utils.Locales.s;
import static org.marid.applib.validators.InputValidators.*;

@Component
@PrototypeScoped
public class RepositoryAddDialog extends ShellDialog {

  private final Function<String, String> validator;
  private final ListenableValue<String> valid;
  private final ListenableValue<String> name = new ListenableValue<>("repository");
  private final ListenableValue<String> selector = new ListenableValue<>();

  public RepositoryAddDialog(PrefShell shell, RepositoryStore store) {
    super(shell);
    setText(s("addRepository"));
    setImage(image(IaIcon.REPOSITORY, 16));
    validator = inputs(fileName(), input(o -> o.filter(store::contains).map(id -> m("duplicateItem", id))));
    valid = new ListenableValue<>(validator.apply(name.get()));
  }

  @Init
  public void name() {
    final var field = addField(s("name"), IaIcon.REPOSITORY, c -> new Text(c, BORDER));
    field.setText(name.get());
    field.addListener(SWT.Modify, e -> {
      valid.set(validator.apply(field.getText()));
      name.set(field.getText());
    });
    ((GridData) field.getLayoutData()).minimumWidth = 100;
    bindValidation(valid, field);
  }

  @Init
  public void selector(RepositoryDao dao) {
    final var field = addField(s("selector"), IaIcon.SELECTOR, c -> new Combo(c, BORDER | DROP_DOWN | READ_ONLY));
    final var entries = dao.selectors().entrySet().stream()
        .peek(e -> field.add(e.getValue().getName() + ": " + e.getValue().getDescription()))
        .collect(toUnmodifiableList());
    field.addListener(SWT.Selection, e -> selector.set(entries.get(field.getSelectionIndex()).getKey()));
    if (field.getItemCount() > 0) {
      field.select(0);
      selector.set(entries.get(0).getKey());
    }
  }

  @Init
  public void cancelButton() {
    addButton(s("cancel"), IaIcon.CANCEL, e -> close());
  }

  @Init
  public void addButton(RepositoryStore store) {
    final var button = addButton(s("add"), IaIcon.ADD, e -> {
      store.add(List.of(new RepositoryItem(name.get()).setSelector(selector.get())));
      close();
    });
    bindEnabled(button, valid.condition(Objects::isNull));
  }
}
