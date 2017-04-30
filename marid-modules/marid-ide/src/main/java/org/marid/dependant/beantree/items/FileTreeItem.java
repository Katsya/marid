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
import org.marid.spring.xml.BeanFile;

import java.util.stream.Collectors;

import static org.marid.ide.common.IdeShapes.fileNode;
import static org.marid.jfx.LocalizedStrings.ls;

/**
 * @author Dmitry Ovchinnikov
 */
public class FileTreeItem extends AbstractTreeItem<BeanFile> {

    private final ObservableValue<String> name;
    private final ObservableValue<String> type;

    public FileTreeItem(BeanFile file) {
        super(file, file.observables());

        name = Bindings.createStringBinding(file::getFilePath, file.path);
        type = ls("file");

        graphicProperty().bind(Bindings.createObjectBinding(() -> fileNode(file, 20), file.observables()));

        getChildren().addAll(file.beans.stream().map(BeanTreeItem::new).collect(Collectors.toList()));
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
        return Bindings.createObjectBinding(() -> null);
    }

    @Override
    public ObservableValue<String> valueText() {
        return Bindings.createStringBinding(elem::getFilePath, elem.observables());
    }
}