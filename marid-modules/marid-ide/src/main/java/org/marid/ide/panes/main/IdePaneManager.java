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

package org.marid.ide.panes.main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.Stage;
import org.marid.ide.menu.IdeMenuItem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Provider;

import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.BORDER_TOP;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.EXIT_TO_APP;
import static org.marid.ide.menu.MenuItemType.CHECK;

/**
 * @author Dmitry Ovchinnikov
 */
@ApplicationScoped
public class IdePaneManager {

    @Produces
    @IdeMenuItem(menu = "Window", text = "Always on top", group = "ops", mdIcons = {BORDER_TOP}, type = CHECK)
    public EventHandler<ActionEvent> alwaysOnTop(Provider<IdePane> idePaneProvider) {
        return event -> {
            final Stage stage = (Stage) idePaneProvider.get().getScene().getWindow();
            final CheckMenuItem menuItem = (CheckMenuItem) event.getSource();
            stage.setAlwaysOnTop(menuItem.isSelected());
        };
    }

    @Produces
    @IdeMenuItem(menu = "File", text = "Exit", group = "x", key = "F12", mdIcons = {EXIT_TO_APP})
    public EventHandler<ActionEvent> exitItem(Provider<IdePane> idePaneProvider) {
        return event -> {
            final Stage stage = (Stage) idePaneProvider.get().getScene().getWindow();
            final Runnable exitTask = (Runnable) stage.getProperties().get("exitTask");
            stage.close();
            exitTask.run();
        };
    }
}