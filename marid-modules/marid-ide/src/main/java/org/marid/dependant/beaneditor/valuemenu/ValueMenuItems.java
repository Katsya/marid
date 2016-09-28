/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.dependant.beaneditor.valuemenu;

import javafx.beans.value.WritableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.marid.IdeDependants;
import org.marid.dependant.beaneditor.BeanListActions;
import org.marid.dependant.beaneditor.BeanMetaInfoProvider;
import org.marid.dependant.beaneditor.listeditor.ListEditorConfiguration;
import org.marid.dependant.beaneditor.propeditor.PropEditorConfiguration;
import org.marid.dependant.beaneditor.valueeditor.ValueEditorConfiguration;
import org.marid.ide.project.ProjectProfileReflection;
import org.marid.jfx.icons.FontIcon;
import org.marid.jfx.icons.FontIcons;
import org.marid.spring.annotation.OrderedInit;
import org.marid.spring.annotation.PrototypeComponent;
import org.marid.spring.xml.data.BeanData;
import org.marid.spring.xml.data.collection.DArray;
import org.marid.spring.xml.data.collection.DElement;
import org.marid.spring.xml.data.collection.DList;
import org.marid.spring.xml.data.collection.DValue;
import org.marid.spring.xml.data.props.DProps;
import org.marid.spring.xml.data.ref.DRef;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.marid.jfx.icons.FontIcon.*;
import static org.marid.jfx.icons.FontIcons.glyphIcon;
import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov
 */
@PrototypeComponent
public class ValueMenuItems {

    private final List<MenuItem> items = new ArrayList<>();
    private final WritableValue<DElement<?>> element;
    private final Type type;

    public ValueMenuItems(WritableValue<DElement<?>> element, Type type) {
        this.element = element;
        this.type = type;
    }

    @OrderedInit(1)
    public void initClearItem() {
        if (element.getValue() != null) {
            final MenuItem clearItem = new MenuItem(s("Clear value"), glyphIcon(M_CLEAR, 16));
            clearItem.setOnAction(ev -> element.setValue(null));
            items.add(clearItem);
            items.add(new SeparatorMenuItem());
        }
    }

    @OrderedInit(2)
    public void initEditValue(IdeDependants dependants) {
        final MenuItem mi = new MenuItem(s("Edit value..."), glyphIcon(M_MODE_EDIT, 16));
        mi.setOnAction(event -> {
            if (!(element.getValue() instanceof DValue)) {
                element.setValue(new DValue());
            }
            dependants.start("valueEditor", dependantBuilder -> dependantBuilder
                    .conf(ValueEditorConfiguration.class)
                    .arg("value", element.getValue())
                    .arg("type", type));
        });
        items.add(mi);
        items.add(new SeparatorMenuItem());
    }

    @OrderedInit(3)
    public void initRefValue(BeanMetaInfoProvider provider, ProjectProfileReflection reflection, BeanListActions actions) {
        final List<MenuItem> refItems = new ArrayList<>();
        final AtomicBoolean changed = new AtomicBoolean();
        reflection.getProfile().getBeanFiles().forEach(p -> p.getValue().beans.forEach(data -> {
            final Optional<? extends Type> t = reflection.getType(data);
            if (t.isPresent()) {
                if (TypeUtils.isAssignable(t.get(), type)) {
                    final MenuItem item = new MenuItem(data.getName(), FontIcons.glyphIcon(FontIcon.M_BEENHERE, 16));
                    item.setOnAction(event -> {
                        final DRef ref = new DRef();
                        ref.setBean(data.getName());
                        element.setValue(ref);
                    });
                    refItems.add(item);
                    changed.compareAndSet(false, true);
                }
            }
        }));
        if (changed.compareAndSet(true, false)) {
            refItems.add(new SeparatorMenuItem());
        }
        provider.beans().forEach((name, definition) -> {
            final Optional<Class<?>> beanClass = reflection.getProfile().getClass(definition.getBeanClassName());
            if (beanClass.isPresent() && TypeUtils.isAssignable(beanClass.get(), type)) {
                final MenuItem item = new MenuItem(name, FontIcons.glyphIcon(FontIcon.M_ACCOUNT_BALANCE, 16));
                item.setOnAction(event -> {
                    final BeanData data = actions.insertItem(name, definition);
                    final DRef ref = new DRef();
                    ref.setBean(data.getName());
                    element.setValue(ref);
                });
                refItems.add(item);
                changed.compareAndSet(false, true);
            }
        });
        if (!refItems.isEmpty()) {
            if (refItems.get(refItems.size() - 1) instanceof SeparatorMenuItem) {
                refItems.remove(refItems.size() - 1);
            }
            final Menu menu = new Menu(s("Reference"), glyphIcon(M_LINK, 16));
            menu.getItems().addAll(refItems);
            items.add(menu);
            items.add(new SeparatorMenuItem());
        }
    }

    @OrderedInit(4)
    public void initPropertiesEdit(IdeDependants dependants) {
        if (type != null && TypeUtils.isAssignable(type, Properties.class)) {
            final MenuItem mi = new MenuItem(s("Edit properties..."), glyphIcon(M_MODE_EDIT, 16));
            mi.setOnAction(e -> {
                if (!(element.getValue() instanceof DProps)) {
                    element.setValue(new DProps());
                }
                dependants.start("propsEditor", dependantBuilder -> dependantBuilder
                        .conf(PropEditorConfiguration.class)
                        .arg("props", element.getValue())
                        .arg("type", type));
            });
            items.add(mi);
            items.add(new SeparatorMenuItem());
        }
    }

    @OrderedInit(5)
    public void initListEdit(IdeDependants dependants) {
        if (type != null && TypeUtils.isAssignable(type, List.class)) {
            final MenuItem mi = new MenuItem(s("Edit list..."), glyphIcon(M_MODE_EDIT, 16));
            mi.setOnAction(event -> {
                if (!(element.getValue() instanceof DList)) {
                    final DList list = new DList();
                    final Map<TypeVariable<?>, Type> map = TypeUtils.getTypeArguments(type, List.class);
                    if (map != null) {
                        map.forEach((v, t) -> {
                            final Class<?> rawType = TypeUtils.getRawType(v, t);
                            if (rawType != null) {
                                list.valueType.setValue(rawType.getName());
                            }
                        });
                    }
                    element.setValue(list);
                }
                dependants.start("listEditor", dependantBuilder -> dependantBuilder
                        .conf(ListEditorConfiguration.class)
                        .arg("collection", element.getValue())
                        .arg("type", type));
            });
            items.add(mi);
            items.add(new SeparatorMenuItem());
        }
    }

    @OrderedInit(6)
    public void initArrayEdit(IdeDependants dependants) {
        if (type != null && TypeUtils.isArrayType(type)) {
            final MenuItem mi = new MenuItem(s("Edit array..."), glyphIcon(M_MODE_EDIT, 16));
            mi.setOnAction(event -> {
                if (!(element.getValue() instanceof DArray)) {
                    final DArray list = new DArray();
                    final Type componentType = TypeUtils.getArrayComponentType(type);
                    final Class<?> rawType = TypeUtils.getRawType(componentType, null);
                    if (rawType != null) {
                        list.valueType.setValue(rawType.getName());
                    }
                    element.setValue(list);
                }
                dependants.start("arrayEditor", dependantBuilder -> dependantBuilder
                        .conf(ListEditorConfiguration.class)
                        .arg("collection", element.getValue())
                        .arg("type", type));
            });
            items.add(mi);
            items.add(new SeparatorMenuItem());
        }
    }

    public void addTo(ContextMenu contextMenu) {
        addTo(contextMenu.getItems());
    }

    public void addTo(Menu menu) {
        addTo(menu.getItems());
    }

    public void addTo(List<MenuItem> menuItems) {
        if (items.isEmpty()) {
            return;
        }
        if (!menuItems.isEmpty()) {
            menuItems.add(new SeparatorMenuItem());
        }
        if (!items.isEmpty()) {
            if (items.get(items.size() - 1) instanceof SeparatorMenuItem) {
                items.remove(items.size() - 1);
            }
        }
        menuItems.addAll(items);
    }
}
