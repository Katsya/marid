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

package org.marid.jfx.menu;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.function.Consumer;

/**
 * @author Dmitry Ovchinnikov
 */
public interface MenuUtils {

    static void addGroup(ObservableList<MenuItem> menuItems, Consumer<ObservableList<MenuItem>> task) {
        final int index = menuItems.size();
        task.accept(menuItems);
        if (menuItems.size() > index && index > 0) {
            menuItems.add(index, new SeparatorMenuItem());
        }
    }
}
