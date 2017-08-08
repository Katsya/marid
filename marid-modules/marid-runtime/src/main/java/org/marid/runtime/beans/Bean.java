/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.runtime.beans;

import org.marid.io.Xmls;
import org.marid.runtime.exception.MaridFilterNotFoundException;
import org.marid.runtime.exception.MaridMethodNotFoundException;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static java.lang.invoke.MethodHandles.publicLookup;
import static org.marid.io.Xmls.attribute;
import static org.marid.misc.Builder.build;

/**
 * @author Dmitry Ovchinnikov
 */
public final class Bean {

    @Nonnull
    public final String name;

    @Nonnull
    public final String factory;

    @Nonnull
    public final BeanMethod producer;

    @Nonnull
    public final BeanMethod[] initializers;

    public Bean(@Nonnull String name,
                @Nonnull String factory,
                @Nonnull BeanMethod producer,
                @Nonnull BeanMethod... initializers) {
        this.name = name;
        this.factory = factory;
        this.producer = producer;
        this.initializers = initializers;
    }

    public Bean(@Nonnull Element element) {
        this.name = attribute(element, "name").orElseThrow(NullPointerException::new);
        this.factory = attribute(element, "factory").orElseThrow(NullPointerException::new);
        this.producer = Xmls.nodes(element, Element.class)
                .filter(e -> "producer".equals(e.getTagName()))
                .map(BeanMethod::new)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No producer for " + element));
        this.initializers = Xmls.nodes(element, Element.class)
                .filter(e -> "initializer".equals(e.getTagName()))
                .map(BeanMethod::new)
                .toArray(BeanMethod[]::new);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{name, factory, producer, initializers});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return true;
        } else if (obj.getClass() != Bean.class) {
            return false;
        } else {
            final Bean that = (Bean) obj;
            return Arrays.deepEquals(
                    new Object[]{this.name, this.factory, this.producer, this.initializers},
                    new Object[]{that.name, that.factory, that.producer, that.initializers}
            );
        }
    }

    public void writeTo(@Nonnull Element element) {
        element.setAttribute("name", name);
        element.setAttribute("factory", factory);

        producer.writeTo(build(element.getOwnerDocument().createElement("producer"), element::appendChild));

        for (final BeanMethod i : initializers) {
            i.writeTo(build(element.getOwnerDocument().createElement("initializer"), element::appendChild));
        }
    }

    public MethodHandle findInitializer(MethodHandle constructor, BeanMethod initializer) {
        final Class<?> targetClass = constructor.type().returnType();
        try {
            return findMethod(targetClass, initializer, false);
        } catch (Throwable x) {
            throw new MaridMethodNotFoundException(name, initializer.name(), x);
        }
    }

    public MethodHandle findProducer(Class<?> targetClass) {
        try {
            return findMethod(targetClass, producer, true);
        } catch (Throwable x) {
            throw new MaridMethodNotFoundException(name, producer.name(), x);
        }
    }

    private MethodHandle findMethod(Class<?> targetClass, BeanMethod producer, boolean getters) throws Exception {
        if (producer.name().equals("new")) {
            for (final Constructor<?> constructor : targetClass.getConstructors()) {
                if (producer.matches(constructor.getParameterTypes())) {
                    return publicLookup().unreflectConstructor(constructor);
                }
            }
        } else {
            for (final Method method : targetClass.getMethods()) {
                if (method.getName().equals(producer.name()) && producer.matches(method.getParameterTypes())) {
                    return publicLookup().unreflect(method);
                }
            }
        }
        if (producer.args.length == (getters ? 0 : 1)) {
            for (final Field field : targetClass.getFields()) {
                if (field.getName().equals(producer.name())) {
                    return getters
                            ? publicLookup().unreflectGetter(field)
                            : publicLookup().unreflectSetter(field);
                }
            }
        }
        throw new IllegalStateException("No producers found for " + producer + " of " + targetClass);
    }

    @Override
    public String toString() {
        return "Bean" + Arrays.deepToString(new Object[]{name, factory, producer, initializers});
    }
}
