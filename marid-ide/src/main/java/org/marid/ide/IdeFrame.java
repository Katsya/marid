/*
 * Copyright (C) 2012 Dmitry Ovchinnikov
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
package org.marid.ide;

import javax.swing.JFrame;
import org.marid.ide.l10n.Localized;

/**
 * IDE frame.
 *
 * @author Dmitry Ovchinnikov (d.ovchinnikow at gmail.com)
 */
public class IdeFrame extends JFrame implements Localized {

    /**
     * Constructs the IDE frame.
     */
    public IdeFrame() {
        super(L.s("Marid IDE"));
    }
}
