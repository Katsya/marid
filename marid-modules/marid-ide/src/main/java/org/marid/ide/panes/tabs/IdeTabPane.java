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

package org.marid.ide.panes.tabs;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.marid.ide.panes.filebrowser.BeanFileBrowserPane;
import org.marid.ide.panes.logging.LoggingTable;
import org.marid.l10n.L10nSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.marid.jfx.ScrollPanes.scrollPane;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class IdeTabPane extends TabPane implements L10nSupport {

    private final LoggingTable loggingPane;
    private final BeanFileBrowserPane beanFileBrowserPane;

    @Autowired
    public IdeTabPane(LoggingTable loggingPane, BeanFileBrowserPane beanFileBrowserPane) {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        getTabs().add(new Tab(s("Log"), scrollPane(this.loggingPane = loggingPane)));
        getTabs().add(new Tab(s("Bean files"), this.beanFileBrowserPane = beanFileBrowserPane));
    }
}
