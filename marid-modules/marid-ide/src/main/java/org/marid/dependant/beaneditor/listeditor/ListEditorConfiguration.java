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

package org.marid.dependant.beaneditor.listeditor;

import javafx.beans.value.ObservableStringValue;
import org.marid.spring.dependant.DependantConfiguration;
import org.marid.spring.xml.DCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ResolvableType;

/**
 * @author Dmitry Ovchinnikov.
 */
@Configuration
@Import({ListEditor.class, ListEditorTab.class})
public class ListEditorConfiguration extends DependantConfiguration<ListEditorParams> {

    @Bean
    public DCollection<?> collection() {
        return param.collection;
    }

    @Bean
    public ResolvableType type() {
        return param.type;
    }

    @Bean
    public ObservableStringValue name() {
        return param.name;
    }
}
