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

package org.marid.dependant.beaneditor.model;

import org.marid.annotation.MetaLiteral;
import org.marid.runtime.beans.Bean;

/**
 * @author Dmitry Ovchinnikov
 */
public class LibraryBean {

    public final String name;
    public final Bean bean;
    public final MetaLiteral literal;

    public LibraryBean(String name, Bean bean, MetaLiteral literal) {
        this.name = name;
        this.bean = bean;
        this.literal = literal;
    }
}
