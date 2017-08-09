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

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import org.marid.ide.model.BeanMethodData;
import org.marid.ide.project.ProjectProfile;
import org.marid.ide.settings.AppearanceSettings;
import org.marid.ide.types.BeanTypeInfo;
import org.marid.ide.types.BeanTypeResolver;
import org.marid.runtime.context.MaridRuntimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.regex.Pattern;

import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class SignatureResolver {

    private static final Pattern LONG_NAME_PREFIX = Pattern.compile("(\\w+[.])++");

    private final AppearanceSettings appearanceSettings;
    private final ProjectProfile profile;
    private final BeanTypeResolver resolver;

    @Autowired
    public SignatureResolver(AppearanceSettings appearanceSettings,
                             ProjectProfile profile,
                             BeanTypeResolver resolver) {
        this.appearanceSettings = appearanceSettings;
        this.profile = profile;
        this.resolver = resolver;
    }

    public ObservableStringValue signature(ObservableValue<BeanMethodData> methodData, Observable... observables) {
        final Observable[] deps = Arrays.copyOf(observables, observables.length + 3);
        deps[observables.length] = profile.getBeanFile().beans;
        deps[observables.length + 1] = appearanceSettings.showFullNamesProperty();
        deps[observables.length + 2] = methodData;
        return Bindings.createStringBinding(() -> textSafe(methodData.getValue()), deps);
    }

    public ObservableStringValue factory(ObservableValue<String> factory) {
        return Bindings.createStringBinding(
                () -> factory.getValue() == null ? null : postProcess(factory.getValue()),
                appearanceSettings.showFullNamesProperty(),
                factory
        );
    }

    private String textSafe(BeanMethodData data) {
        if (data == null) {
            return null;
        }
        try {
            final BeanTypeInfo typeInfo = resolver.resolve(profile.getBeanContext(), data.parent.getName());
            final Type[] types = typeInfo.getParameters(data);
            return postProcess(MaridRuntimeUtils.toCanonicalWithArgs(data.getSignature(), types));
        } catch (Exception x) {
            log(WARNING, "Unable to get generic signature", x);
        }
        return postProcess(data.getSignature());
    }

    public String postProcess(String type) {
        if (!appearanceSettings.showFullNamesProperty().get()) {
            type = LONG_NAME_PREFIX.matcher(type).replaceAll("");
        }
        return type;
    }
}
