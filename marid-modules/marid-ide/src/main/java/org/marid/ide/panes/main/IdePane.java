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

import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.marid.ide.menu.IdeMenuToolbarPane;
import org.marid.ide.status.IdeStatusBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class IdePane extends BorderPane {

    @Autowired
    public IdePane(TabPane ideTabPane, IdeMenuToolbarPane ideMenuToolbarPane, IdeStatusBar ideStatusBar) {
        super(ideTabPane, ideMenuToolbarPane, null, ideStatusBar, null);
    }
}
