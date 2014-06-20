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

package org.marid.bd;

import images.Images;
import org.marid.bd.components.StandardBlockComponent;

import javax.swing.*;

/**
 * @author Dmitry Ovchinnikov
 */
public abstract class StatelessBlock extends Block {

    protected final String name;
    protected final ImageIcon visualRepresentation;

    public StatelessBlock(String name, String icon) {
        this(name, Images.getIcon(icon));
    }

    public StatelessBlock(String name, ImageIcon icon) {
        this.name = name;
        this.visualRepresentation = icon;
    }

    @Override
    public BlockComponent createComponent() {
        return new StandardBlockComponent<>(this, c -> c.add(new JLabel(getVisualRepresentation())));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ImageIcon getVisualRepresentation() {
        return visualRepresentation;
    }
}
