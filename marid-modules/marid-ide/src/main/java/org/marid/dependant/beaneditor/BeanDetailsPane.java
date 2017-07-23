/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.marid.dependant.beaneditor;

import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import org.marid.jfx.icons.FontIcons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.marid.jfx.LocalizedStrings.ls;
import static org.marid.misc.Builder.build;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanDetailsPane extends Accordion {

    private final TitledPane argsPane;
    private final TitledPane initializersPane;

    public BeanDetailsPane() {
        argsPane = build(new TitledPane(), pane -> {
            pane.textProperty().bind(ls("Arguments"));
            pane.setGraphic(FontIcons.glyphIcon("D_DISQUS", 16));
            getPanes().add(pane);
            setExpandedPane(pane);
        });

        initializersPane = build(new TitledPane(), pane -> {
            pane.textProperty().bind(ls("Initializers"));
            pane.setGraphic(FontIcons.glyphIcon("D_STAR", 16));
            getPanes().add(pane);
        });
    }

    @Autowired
    private void initArgsPane(BeanArgsTable argTable) {
        argsPane.setContent(argTable);
    }
}
