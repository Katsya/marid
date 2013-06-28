/*
 * Copyright (C) 2013 Dmitry Ovchinnikov
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

package org.marid.ide.itf;

import java.util.prefs.Preferences;

import static org.marid.methods.PrefMethods.*;

/**
 * @author Dmitry Ovchinnikov
 */
public interface Graphical {

    public static final Preferences SYSPREF = preferences("preferences");

    public int getWidth();

    public int getHeight();

    public String getName();

    public boolean isVisible();

    public void setVisible(boolean state);
}