/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.jfx.table;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.marid.jfx.action.FxAction;
import org.marid.jfx.action.MaridActions;
import org.marid.jfx.action.SpecialAction;
import org.marid.jfx.action.SpecialActions;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dmitry Ovchinnikov
 */
public class MaridTableView<T> extends TableView<T> {

    private final List<SpecialAction> specialActions = new ArrayList<>();

    public MaridTableView(ObservableList<T> list) {
        super(list);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    }

    @Resource
    public void setSpecialActions(SpecialActions specialActions) {
        this.specialActions.clear();
        this.specialActions.addAll(specialActions.getActionList());
    }

    protected void initialize(Initializer initializer) {
        setRowFactory(table -> {
            final TableRow<T> row = initializer.rowSupplier.get();
            row.focusedProperty().addListener((observable, oldValue, newValue) -> {
                final MenuItem[] items = MaridActions.contextMenu(initializer.tableActions.apply(row.getItem()));
                row.setContextMenu(items.length == 0 ? null : new ContextMenu(items));
            });
            return row;
        });
        getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            final Collection<FxAction> actions = initializer.tableActions.apply(newValue);
            initializer.assign(initializer.group(actions));
        });
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                final Collection<FxAction> actions = initializer.tableActions.apply(null);
                setContextMenu(new ContextMenu(MaridActions.contextMenu(actions)));
                initializer.assign(initializer.group(actions));
            } else {
                setContextMenu(null);
                initializer.reset(Collections.emptySet());
            }
        });
    }

    public class Initializer {

        Function<T, Collection<FxAction>> tableActions = e -> Collections.emptyList();
        Supplier<TableRow<T>> rowSupplier = TableRow::new;

        public Initializer setRowSupplier(Supplier<TableRow<T>> rowSupplier) {
            this.rowSupplier = rowSupplier;
            return this;
        }

        public Initializer setTableActions(Function<T, Collection<FxAction>> tableActions) {
            this.tableActions = tableActions;
            return this;
        }

        protected void reset(Set<SpecialAction> set) {
            for (final SpecialAction action : specialActions) {
                if (!set.contains(action)) {
                    action.reset();
                    action.update();
                }
            }
        }

        void assign(Map<SpecialAction, Collection<FxAction>> map) {
            specialActions.forEach(a -> {
                a.reset();
                a.update();
            });
            map.forEach((k, v) -> {
                k.reset();
                if (v.size() == 1) {
                    k.copy(v.iterator().next());
                } else {
                    k.children.addAll(v);
                }
                k.update();
            });
            reset(map.keySet());
        }

        protected Map<SpecialAction, Collection<FxAction>> group(Collection<FxAction> actions) {
            return actions.stream()
                    .filter(v -> v.specialAction != null)
                    .collect(Collectors.groupingBy(v -> v.specialAction, Collectors.toCollection(ArrayList::new)));
        }
    }
}
