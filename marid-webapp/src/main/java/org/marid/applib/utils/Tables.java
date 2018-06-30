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
package org.marid.applib.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.controls.pane.TablePane;
import org.marid.applib.dao.ListStore;
import org.marid.applib.image.IaIcon;
import org.marid.applib.model.Elem;

import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static org.eclipse.swt.SWT.Selection;
import static org.marid.applib.dao.ListStore.EventType.*;

public interface Tables {

  static void autoResizeColumns(Table table) {
    table.addListener(SWT.Resize, e -> {
      final int sum = IntStream.range(0, table.getColumnCount()).map(i -> table.getColumn(i).getWidth()).sum();
      final int width = table.getBounds().width;
      for (int i = 0; i < table.getColumnCount(); i++) {
        final var column = table.getColumn(i);
        column.setWidth((width * column.getWidth()) / sum);
      }
    });
  }

  static void addStdButtons(TablePane table, ListStore<?, ?, ?> store) {
    {
      final var item = table.addItem(toolBar -> new ToolItem(toolBar, SWT.PUSH));
      item.setImage(table.image(IaIcon.REMOVE));
      item.addListener(SWT.Selection, e -> store.remove(table.getSelectionManager().getSelected()));
      table.enableOnSelection(item::setEnabled);
    }
    table.addSeparator();
    {
      final var item = table.addItem(toolBar -> new ToolItem(toolBar, SWT.PUSH));
      item.setImage(table.image(IaIcon.REFRESH));
      item.addListener(Selection, e -> store.refresh());
    }
    {
      final var item = table.addItem(toolBar -> new ToolItem(toolBar, SWT.PUSH));
      item.setImage(table.image(IaIcon.SAVE));
      item.addListener(Selection, e -> store.save());
    }
    table.addSeparator();
    {
      table.addSelectAllButton();
      table.addDeselectAllButton();
    }
    table.addSeparator();
  }

  static <I, T extends Elem<I>> void init(TablePane table, ListStore<I, T, ?> store, BiConsumer<TableItem, T> configurer) {
    store.addListener(ADD, e -> e.update.forEach((index, v) -> {
      final var item = new TableItem(table.getTable(), SWT.NONE, index);
      configurer.accept(item, v);
      table.markDirty();
    }));
    store.addListener(REMOVE, e -> e.update.descendingMap().forEach((index, v) -> {
      table.getTable().remove(index);
      table.markDirty();
    }));
    store.addListener(UPDATE, e -> e.update.forEach((index, v) -> {
      final TableItem item = table.getTable().getItem(index);
      configurer.accept(item, v);
    }));
    store.refresh();
  }
}
