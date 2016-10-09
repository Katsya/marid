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

package org.marid.dependant.beaneditor.listeditor;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import org.marid.dependant.beaneditor.valuemenu.ValueMenuItems;
import org.marid.jfx.icons.FontIcon;
import org.marid.jfx.icons.FontIcons;
import org.marid.jfx.props.WritableValueImpl;
import org.marid.spring.xml.collection.DCollection;
import org.marid.spring.xml.collection.DElement;
import org.marid.spring.xml.collection.DList;
import org.marid.spring.xml.collection.DValue;
import org.marid.spring.xml.props.DProps;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov.
 */
@Component
public class ListEditor extends ListView<DElement<?>> {

    @Autowired
    public ListEditor(DCollection<?> collection) {
        super(collection.elements);
    }

    @Autowired
    public void initCellFactory(Type type, ObjectProvider<ValueMenuItems> items) {
        setCellFactory(param -> new TextFieldListCell<DElement<?>>() {
            @Override
            public void updateItem(DElement<?> item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setContextMenu(null);
                    setGraphic(null);
                } else {
                    if (item instanceof DValue) {
                        setText(((DValue) item).getValue());
                        setGraphic(FontIcons.glyphIcon(FontIcon.D_COMMENT_TEXT, 16));
                    } else if (item instanceof DList) {
                        setText(s("<list>"));
                        setGraphic(FontIcons.glyphIcon(FontIcon.M_LIST, 16));
                    } else if (item instanceof DProps) {
                        setText(s("<props>"));
                        setGraphic(FontIcons.glyphIcon(FontIcon.D_VIEW_LIST, 16));
                    }
                    final Consumer<DElement<?>> consumer = e -> getItems().set(getIndex(), e);
                    final Supplier<DElement<?>> supplier = () -> getItems().get(getIndex());
                    final ContextMenu contextMenu = new ContextMenu();
                    items.getObject(new WritableValueImpl<>(consumer, supplier), type).addTo(contextMenu);
                    setContextMenu(contextMenu);
                }
            }
        });
    }
}