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

package org.marid.bd.blocks.meta;

import org.codehaus.groovy.ast.ClassNode;
import org.marid.bd.BlockColors;
import org.marid.bd.ConfigurableBlock;
import org.marid.bd.StandardBlock;
import org.marid.bd.blocks.BdBlock;
import org.marid.bd.components.AbstractBlockComponentEditor;
import org.marid.swing.input.ClassInputControl;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.EventListener;
import java.util.Objects;

/**
 * @author Dmitry Ovchinnikov
 */
@BdBlock(name = "Class Block", label = "class", color = BlockColors.ANNOTATIONS_BLOCK_COLOR)
@XmlRootElement
public class ClassBlock extends StandardBlock implements ConfigurableBlock {

    @XmlAttribute
    protected Class<?> targetClass;

    public final Out out = new Out("out", ClassNode.class, () -> new ClassNode(targetClass));

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> newClass) {
        if (!Objects.equals(targetClass, newClass)) {
            targetClass = newClass;
            fireEvent(ClassBlockListener.class, l -> l.classChanged(targetClass));
        }
    }

    @Override
    public String getLabel() {
        return targetClass == null ? "class" : targetClass.getCanonicalName();
    }

    @Override
    public void reset() {
    }

    @Override
    public Window createWindow(Window parent) {
        final ClassInputControl classInputControl = new ClassInputControl();
        classInputControl.setInputValue(targetClass);
        return new AbstractBlockComponentEditor<ClassBlock>(parent, this) {
            {
                tabPane("Common").addLine("Class name", classInputControl);
            }

            @Override
            protected void onSubmit(Action action, ActionEvent actionEvent) throws Exception {
                setTargetClass(classInputControl.getInputValue());
            }
        };
    }

    public interface ClassBlockListener extends EventListener {

        void classChanged(Class<?> value);
    }
}
