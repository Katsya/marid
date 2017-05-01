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
import org.jetbrains.annotations.NotNull;
import org.marid.ide.project.ProjectProfile;
import org.marid.jfx.icons.FontIcon;
import org.marid.jfx.icons.FontIcons;
import org.marid.spring.xml.BeanData;
import org.marid.spring.xml.BeanProp;
import org.marid.util.MethodUtils;
import org.springframework.core.annotation.Order;

import static java.lang.Integer.compare;
import static java.util.Optional.ofNullable;

/**
 * @author Dmitry Ovchinnikov
 */
@Order(2)
public class PropertyTreeItem extends AbstractTreeItem<BeanProp> implements Comparable<AbstractTreeItem<?>> {

    public PropertyTreeItem(BeanProp elem) {
        super(elem);

        setGraphic(FontIcons.glyphIcon(FontIcon.D_CLOUD_CIRCLE, 20));
    }

    @Override
    public ObservableValue<String> getName() {
        return elem.name;
    }

    @Override
    public ObservableValue<String> getType() {
        return Bindings.createStringBinding(() -> {
            final ProjectProfile profile = getProfile();
            final BeanData data = find(BeanTreeItem.class).elem;
            return MethodUtils.readableType(profile.getPropType(data, elem.getName()));
        }, elem.observables());
    }

    @Override
    public ObservableValue<Node> valueGraphic() {
        return Bindings.createObjectBinding(() -> {
            final Label label = new Label();
            return label;
        }, elem.data);
    }

    @Override
    public ObservableValue<String> valueText() {
        return Bindings.createStringBinding(() -> null);
    }

    @Override
    public int compareTo(@NotNull AbstractTreeItem<?> o) {
        if (o instanceof PropertyTreeItem) {
            final PropertyTreeItem i = (PropertyTreeItem) o;
            final BeanData beanData = find(BeanTreeItem.class).elem;
            final int i1 = beanData.properties.indexOf(elem);
            final int i2 = beanData.properties.indexOf(i.elem);
            return compare(i1, i2);
        } else {
            final Order o1 = getClass().getAnnotation(Order.class);
            final Order o2 = o.getClass().getAnnotation(Order.class);
            return compare(ofNullable(o1).map(Order::value).orElse(0), ofNullable(o2).map(Order::value).orElse(0));
        }
    }
}
