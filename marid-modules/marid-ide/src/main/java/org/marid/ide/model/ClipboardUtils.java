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

package org.marid.ide.model;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.marid.io.Xmls;
import org.marid.runtime.beans.Bean;

import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.marid.io.Xmls.writeFormatted;

public interface ClipboardUtils {

    DataFormat BEAN_DATA_FORMAT = new DataFormat("text/x-bean-data");

    static boolean hasBeanData(Clipboard clipboard) {
        return clipboard.hasContent(BEAN_DATA_FORMAT);
    }

    static Optional<Bean> load(Clipboard clipboard) {
        if (hasBeanData(clipboard)) {
            final String xml = clipboard.getContent(BEAN_DATA_FORMAT).toString();
            try (final StringReader reader = new StringReader(xml)) {
                return of(Xmls.read(reader, Bean::new));
            }
        } else {
            return empty();
        }
    }

    static void save(Bean bean, Clipboard clipboard) {
        final StringWriter writer = new StringWriter();
        writeFormatted("beans", bean::writeTo, new StreamResult(writer));
        clipboard.setContent(singletonMap(BEAN_DATA_FORMAT, writer.toString()));
    }
}