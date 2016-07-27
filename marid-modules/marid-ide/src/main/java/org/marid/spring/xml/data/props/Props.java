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

package org.marid.spring.xml.data.props;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.marid.ide.project.ProjectProfile;
import org.marid.spring.xml.data.AbstractData;
import org.marid.spring.xml.data.BeanLike;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Executable;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static org.marid.spring.xml.MaridBeanUtils.setAttr;
import static org.marid.spring.xml.MaridBeanUtils.setProperty;

/**
 * @author Dmitry Ovchinnikov.
 */
public class Props extends AbstractData<Props> implements BeanLike {

    public final StringProperty id = new SimpleStringProperty(this, "id");
    public final StringProperty valueType = new SimpleStringProperty(this, "value-type", String.class.getName());
    public final StringProperty location = new SimpleStringProperty(this, "location");
    public final StringProperty localOverride = new SimpleStringProperty(this, "local-override");
    public final StringProperty ignoreResourceNotFound = new SimpleStringProperty(this, "ignore-resource-not-found");
    public final ObservableList<PropertyEntry> entries = FXCollections.observableArrayList();

    @Override
    public Stream<? extends Executable> getConstructors(ProjectProfile profile) {
        return Stream.empty();
    }

    @Override
    public Optional<Class<?>> getClass(ProjectProfile profile) {
        return Optional.of(Properties.class);
    }

    @Override
    public void updateBeanData(ProjectProfile profile) {
    }

    @Override
    public StringProperty nameProperty() {
        return id;
    }

    @Override
    public void save(Node node, Document document) {
        final Element element = document.createElement("props");
        node.appendChild(element);

        setAttr(id, element);
        setAttr(ignoreResourceNotFound, element);
        setAttr(localOverride, element);
        setAttr(valueType, element);
        setAttr(location, element);

        entries.forEach(entry -> entry.save(element, document));
    }

    @Override
    public void load(Node node, Document document) {
        final Element element = (Element) node;

        setProperty(id, element);
        setProperty(valueType, element);
        setProperty(location, element);
        setProperty(localOverride, element);
        setProperty(ignoreResourceNotFound, element);

        final NodeList children = element.getElementsByTagName("prop");
        for (int i = 0; i < children.getLength(); i++) {
            final Element e = (Element) children.item(i);
            final PropertyEntry entry = new PropertyEntry();
            entry.load(e, document);
            entries.add(entry);
        }
    }
}
