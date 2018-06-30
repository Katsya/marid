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
package org.marid.applib.controls.pane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.image.IaIcon;
import org.marid.applib.selection.SelectionManager;
import org.marid.applib.utils.Tables;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static org.eclipse.swt.SWT.*;

public abstract class TablePane extends Pane {

  protected final Table table;
  protected final SelectionManager selectionManager;
  protected final ConcurrentLinkedQueue<Runnable> dataListeners = new ConcurrentLinkedQueue<>();

  public TablePane(Composite parent, int style, int toolbarStyle, int tableStyle) {
    super(parent, style, toolbarStyle);
    table = new Table(this, tableStyle);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    selectionManager = new SelectionManager(table);

    Tables.autoResizeColumns(table);
  }

  public TablePane(Composite parent) {
    this(parent, NONE, BORDER | WRAP | FLAT, BORDER | V_SCROLL | H_SCROLL | CHECK);
  }

  @SafeVarargs
  protected final void addColumn(String text, int width, Consumer<TableColumn>... consumers) {
    final var column = new TableColumn(table, NONE);
    column.setText(text);
    column.setWidth(width);
    for (final var consumer : consumers) {
      consumer.accept(column);
    }
  }

  public void markDirty() {
    dataListeners.forEach(Runnable::run);
  }

  public void addSelectAllButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.SELECT_ALL));
    item.addListener(Selection, e -> selectionManager.selectAll());
    enableOnNonEmpty(item::setEnabled);
  }

  public void addDeselectAllButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.DESELECT_ALL));
    item.addListener(Selection, e -> selectionManager.deselectAll());
    enableOnNonEmpty(item::setEnabled);
  }

  public void enableOnSelection(Consumer<Boolean> enabled) {
    table.addListener(Selection, e -> enabled.accept(selectionManager.isSelected()));
    dataListeners.add(() -> enabled.accept(selectionManager.isSelected()));
    enabled.accept(selectionManager.isSelected());
  }

  public void enableOnNonEmpty(Consumer<Boolean> enabled) {
    enabled.accept(table.getItemCount() > 0);
    dataListeners.add(() -> enabled.accept(table.getItemCount() > 0));
  }

  public SelectionManager getSelectionManager() {
    return selectionManager;
  }

  public Table getTable() {
    return table;
  }
}
