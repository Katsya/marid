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

package org.marid.jfx.props;

import javafx.beans.property.*;
import javafx.event.Event;
import javafx.event.EventHandler;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Dmitry Ovchinnikov
 */
public interface Props {

    static StringProperty stringProp(Supplier<String> supplier, Consumer<String> consumer) {
        final StringProperty property = new SimpleStringProperty(supplier.get());
        property.addListener((observable, oldValue, newValue) -> consumer.accept(newValue));
        return property;
    }

    static BooleanProperty boolProp(BooleanSupplier supplier, Consumer<Boolean> consumer) {
        final BooleanProperty property = new SimpleBooleanProperty(supplier.getAsBoolean());
        property.addListener((observable, oldValue, newValue) -> consumer.accept(newValue));
        return property;
    }

    static <E extends Event> void addHandler(Property<EventHandler<E>> property, EventHandler<E> handler) {
        final EventHandler<E> old = property.getValue();
        if (old == null) {
            property.setValue(handler);
        } else {
            property.setValue(event -> {
                old.handle(event);
                handler.handle(event);
            });
        }
    }
}
