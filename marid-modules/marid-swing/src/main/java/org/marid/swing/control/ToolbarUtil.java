/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
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

package org.marid.swing.control;

import javax.swing.*;
import java.awt.*;

/**
 * @author Dmitry Ovchinnikov
 */
public class ToolbarUtil {

    public static int getToolbarOrientation(String borderLayoutOrientation) {
        switch (borderLayoutOrientation) {
            case BorderLayout.LINE_END:
            case BorderLayout.LINE_START:
            case BorderLayout.EAST:
            case BorderLayout.WEST:
                return JToolBar.VERTICAL;
            default:
                return JToolBar.HORIZONTAL;
        }
    }
}
