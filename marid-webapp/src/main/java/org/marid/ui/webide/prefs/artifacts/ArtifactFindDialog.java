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
package org.marid.ui.webide.prefs.artifacts;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.marid.applib.dialogs.ShellDialog;
import org.marid.applib.image.IaIcon;
import org.marid.applib.repository.Artifact;
import org.marid.applib.repository.Repository;
import org.marid.misc.ListenableValue;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.init.Init;
import org.marid.ui.webide.prefs.PrefShell;
import org.marid.ui.webide.prefs.repositories.RepositoryStore;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.eclipse.swt.SWT.*;
import static org.marid.applib.utils.Locales.s;
import static org.marid.misc.Condition.and;
import static org.marid.misc.Condition.or;
import static org.marid.misc.ListenableValue.conditions;

@Component
@PrototypeScoped
public class ArtifactFindDialog extends ShellDialog {

  private final TabFolder tabs;
  private final Composite findTab;
  private final Composite resultTab;
  private final TabItem findTabItem;
  private final TabItem resultTabItem;
  private final ListenableValue<String> artifact = new ListenableValue<>("");
  private final ListenableValue<String> group = new ListenableValue<>("");
  private final ListenableValue<String> className = new ListenableValue<>("");
  private final ListenableValue<String> validArtifact = new ListenableValue<>();
  private final ListenableValue<String> validGroup = new ListenableValue<>();
  private final ListenableValue<String> validClassName = new ListenableValue<>();
  private final ListenableValue<List<Artifact>> artifacts = new ListenableValue<>(List.of());

  public ArtifactFindDialog(PrefShell shell) {
    super(shell, TITLE | CLOSE | APPLICATION_MODAL);
    tabs = new TabFolder(this, TOP);
    tabs.setLayoutData(new GridData(GridData.FILL_BOTH));
    findTab = new Composite(tabs, NONE);
    findTab.setLayoutData(new GridData(GridData.FILL_BOTH));
    findTab.setLayout(new GridLayout(1, false));
    resultTab = new Composite(tabs, NONE);
    resultTab.setLayoutData(new GridData(GridData.FILL_BOTH));
    resultTab.setLayout(new GridLayout(1, false));

    findTabItem = new TabItem(tabs, NONE);
    findTabItem.setControl(findTab);
    findTabItem.setText(s("find"));
    findTabItem.setImage(image(IaIcon.FIND, 16));

    resultTabItem = new TabItem(tabs, NONE);
    resultTabItem.setControl(resultTab);
    resultTabItem.setText(s("artifacts"));
    resultTabItem.setImage(image(IaIcon.ARTIFACT, 16));
  }

  @Init
  public void artifact() {
    final var field = addField(findTab, "artifactId", IaIcon.ARTIFACT, c -> new Text(c, BORDER));
    field.addListener(Modify, e -> artifact.set(field.getText()));
  }

  @Init
  public void groupId() {
    final var field = addField(findTab, "groupId", IaIcon.GROUP, c -> new Text(c, BORDER));
    field.addListener(Modify, e -> group.set(field.getText()));
  }

  @Init
  public void className() {
    final var field = addField(findTab, "class", IaIcon.CLASS, c -> new Text(c, BORDER));
    field.addListener(Modify, e -> className.set(field.getText()));
  }

  @Init
  public void artifacts() {
    final var form = form(resultTab);
  }

  @Init
  public void findButton(RepositoryStore repositoryStore) {
    final var button = addButton(s("find"), IaIcon.ADD, e -> {
      tabs.setSelection(resultTabItem);

      final var finders = repositoryStore.repositories().stream()
          .map(Repository::getArtifactFinder)
          .collect(Collectors.toUnmodifiableList());

      final var artifacts = finders.stream()
          .map(f -> f.find(group.get(), artifact.get(), className.get()))
          .flatMap(Collection::stream)
          .collect(Collectors.toUnmodifiableList());

      this.artifacts.set(artifacts);
    });
    final var validFields = and(conditions(Objects::isNull, validArtifact, validGroup, validClassName));
    final var atLeastOneNonEmptyField = or(conditions(s -> !s.isEmpty(), artifact, group, className));
    bindEnabled(button, validFields.and(atLeastOneNonEmptyField));
  }
}
