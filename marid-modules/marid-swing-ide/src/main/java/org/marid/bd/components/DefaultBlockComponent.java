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

package org.marid.bd.components;

import org.marid.bd.Block;
import org.marid.bd.BlockComponent;
import org.marid.bd.BlockListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;

/**
 * @author Dmitry Ovchinnikov
 */
public abstract class DefaultBlockComponent<B extends Block> extends JPanel implements BlockComponent {

    protected final B block;

    public DefaultBlockComponent(LayoutManager layoutManager, B block) {
        super(layoutManager);
        this.block = block;
        setBorder(new DefaultBlockComponentBorder());
        enableEvents(HierarchyEvent.HIERARCHY_EVENT_MASK);
        setOpaque(false);
    }

    @Override
    protected void processHierarchyEvent(HierarchyEvent e) {
        super.processHierarchyEvent(e);
        if (this instanceof BlockListener && e.getID() == HierarchyEvent.HIERARCHY_CHANGED) {
            if (e.getChangedParent() != null) {
                block.addEventListener(this, (BlockListener) this);
            } else {
                block.removeEventListeners(this);
            }
        }
    }

    @Override
    public B getBlock() {
        return block;
    }

    protected class DefaultInput extends JToggleButton implements Input {

        protected final Block.Input<?> input;

        public DefaultInput(Block.Input<?> input) {
            super(input.getName());
            this.input = input;
        }

        @Override
        public Block.Input<?> getInput() {
            return input;
        }

        @Override
        public DefaultBlockComponent getBlockComponent() {
            return DefaultBlockComponent.this;
        }
    }

    protected class DefaultOutput extends JToggleButton implements Output {

        protected final Block.Output<?> output;

        public DefaultOutput(Block.Output<?> output) {
            super(output.getName());
            this.output = output;
        }

        @Override
        public Block.Output<?> getOutput() {
            return output;
        }

        @Override
        public DefaultBlockComponent getBlockComponent() {
            return DefaultBlockComponent.this;
        }
    }
}