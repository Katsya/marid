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
package org.marid.applib.selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.of;
import static java.util.stream.IntStream.range;

public class SelectionManager {

  private final Supplier<int[]> selected;
  private final BooleanSupplier hasChecked;
  private final Supplier<int[]> checked;
  private final Runnable selectAll;
  private final Runnable deselectAll;
  private final Consumer<int[]> select;
  private final IntSupplier count;

  public SelectionManager(Table table) {
    selected = table::getSelectionIndices;
    if ((table.getStyle() & SWT.CHECK) != 0) {
      checked = () -> range(0, table.getItemCount()).filter(i -> table.getItem(i).getChecked()).toArray();
      hasChecked = () -> range(0, table.getItemCount()).anyMatch(i -> table.getItem(i).getChecked());
      selectAll = () -> range(0, table.getItemCount()).mapToObj(table::getItem).forEach(i -> i.setChecked(true));
      deselectAll = () -> range(0, table.getItemCount()).mapToObj(table::getItem).forEach(i -> i.setChecked(false));
      select = s -> of(s).mapToObj(table::getItem).forEach(i -> i.setChecked(true));
    } else if ((table.getStyle() & SWT.MULTI) != 0) {
      checked = null;
      hasChecked = null;
      selectAll = table::selectAll;
      deselectAll = table::deselectAll;
      select = table::select;
    } else {
      checked = null;
      hasChecked = null;
      selectAll = table::selectAll;
      deselectAll = table::deselectAll;
      select = s -> of(s).findFirst().ifPresent(table::select);
    }
    count = table::getItemCount;
  }

  public SelectionManager(Combo combo) {
    selected = () -> new int[] {combo.getSelectionIndex()};
    checked = null;
    hasChecked = null;
    selectAll = () -> {};
    deselectAll = combo::deselectAll;
    select = s -> IntStream.of(s).findAny().ifPresent(combo::select);
    count = combo::getItemCount;
  }

  public SelectionManager(List list) {
    selected = list::getSelectionIndices;
    checked = null;
    hasChecked = null;
    selectAll = list::selectAll;
    deselectAll = list::deselectAll;
    if ((list.getStyle() & SWT.MULTI) != 0) {
      select = list::select;
    } else {
      select = s -> of(s).findFirst().ifPresent(list::select);
    }
    count = list::getItemCount;
  }

  public boolean isSelected() {
    if (hasChecked != null) {
      return hasChecked.getAsBoolean() || selected.get().length > 0;
    } else {
      return selected.get().length > 0;
    }
  }

  public void selectAll() {
    selectAll.run();
  }

  public void deselectAll() {
    deselectAll.run();
  }

  public void select(int... indices) {
    select.accept(indices);
  }

  public int[] getSelected() {
    if (checked != null) {
      return hasChecked.getAsBoolean() ? checked.get() : selected.get();
    } else {
      return selected.get();
    }
  }

  public int getCount() {
    return count.getAsInt();
  }
}
