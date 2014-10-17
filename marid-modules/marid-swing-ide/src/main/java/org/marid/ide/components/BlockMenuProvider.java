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

package org.marid.ide.components;

import images.Images;
import org.marid.bd.Block;
import org.marid.dyn.MetaInfo;
import org.marid.l10n.L10nSupport;
import org.marid.swing.dnd.DndSource;
import org.marid.swing.dnd.MaridTransferHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.util.*;

import static java.util.Comparator.comparing;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BlockMenuProvider implements L10nSupport {

    private final GenericApplicationContext applicationContext;

    @Autowired
    public BlockMenuProvider(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void fillMenu(JMenu menu) {
        final Map<Package, Set<Block>> map = new TreeMap<>(comparing(Package::getName));
        final Collection<Block> blocks = applicationContext.getBeansOfType(Block.class).values();
        blocks.forEach(b ->
                map.computeIfAbsent(b.getClass().getPackage(), v ->
                        new TreeSet<>(comparing(Block::getName))
                ).add(b)
        );
        map.forEach((group, blockSet) -> {
            final MetaInfo metaInfo = group.getAnnotation(MetaInfo.class);
            final JMenu groupMenu = new JMenu(s(metaInfo != null ? metaInfo.name() : group.getName()));
            if (metaInfo != null && !metaInfo.icon().isEmpty()) {
                groupMenu.setIcon(Images.getIcon(metaInfo.icon(), 22));
            }
            menu.add(groupMenu);
            blockSet.forEach(block -> groupMenu.add(new BlockMenuItem(block)));
        });
    }

    protected static class BlockMenuItem extends JMenuItem implements DndSource<Block>, MenuDragMouseListener {

        protected final Block block;

        public BlockMenuItem(Block block) {
            super(block.getName(), block.getVisualRepresentation(22, 22));
            this.block = block;
            setTransferHandler(new MaridTransferHandler());
            addMenuDragMouseListener(this);
            addActionListener(e -> {
                final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                getTransferHandler().exportToClipboard(this, clipboard, DND_COPY);
            });
        }

        @Override
        public int getDndActions() {
            return DND_COPY;
        }

        @Override
        public Block getDndObject() {
            return block;
        }

        @Override
        public void menuDragMouseEntered(MenuDragMouseEvent e) {

        }

        @Override
        public void menuDragMouseExited(MenuDragMouseEvent e) {

        }

        @Override
        public void menuDragMouseDragged(MenuDragMouseEvent e) {
            getTransferHandler().exportAsDrag(this, e, DND_COPY);
        }

        @Override
        public void menuDragMouseReleased(MenuDragMouseEvent e) {

        }
    }
}