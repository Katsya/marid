/*
 * Copyright (c) 2017 Dmitry Ovchinnikov
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

package org.marid.dependant.beantree.items;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.marid.spring.beans.MaridBeanUtils;
import org.marid.spring.xml.BeanData;
import org.marid.spring.xml.BeanFile;

import static org.marid.ide.common.IdeShapes.fileNode;
import static org.marid.jfx.LocalizedStrings.fs;
import static org.marid.jfx.LocalizedStrings.ls;
import static org.marid.jfx.icons.FontIcon.D_STAR_CIRCLE;
import static org.marid.jfx.icons.FontIcon.D_STAR_OUTLINE;
import static org.marid.jfx.icons.FontIcons.glyphIcon;

/**
 * @author Dmitry Ovchinnikov
 */
public class FileTreeItem extends AbstractTreeItem<BeanFile> {

    private final ObservableValue<String> name;
    private final ObservableValue<String> type;
    private final ListSynchronizer<BeanData, BeanTreeItem> listSynchronizer;

    public FileTreeItem(BeanFile file) {
        super(file);

        name = Bindings.createStringBinding(file::getFilePath, file.path);
        type = ls("file");

        graphicProperty().bind(Bindings.createObjectBinding(() -> fileNode(file, 20), file.observables()));

        listSynchronizer = new ListSynchronizer<>(file.beans, getChildren(), BeanTreeItem::new);
        setExpanded(true);
    }

    @Override
    public ObservableValue<String> getName() {
        return name;
    }

    @Override
    public ObservableValue<String> getType() {
        return type;
    }

    @Override
    public ObservableValue<Node> valueGraphic() {
        return Bindings.createObjectBinding(() -> {
            final HBox box = new HBox(10);
            {
                {
                    final Label label = new Label();
                    label.setGraphic(glyphIcon(D_STAR_CIRCLE, 20));
                    label.textProperty().bind(fs("%s: %d", ls("Beans"), elem.beans.size()));
                    box.getChildren().add(label);
                }
                {
                    final Label label = new Label();
                    label.setGraphic(glyphIcon(D_STAR_OUTLINE, 20));
                    final long count = elem.beans.stream().flatMap(MaridBeanUtils::beans).count();
                    label.textProperty().bind(fs("%s: %d", ls("Internal Beans"), count));
                    box.getChildren().add(label);
                }
            }
            return box;
        }, elem.observables());
    }

    @Override
    public ObservableValue<String> valueText() {
        return Bindings.createStringBinding(() -> null);
    }
}
