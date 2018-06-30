/*-
 * #%L
 * marid-util
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.STATIC;
import static java.lang.reflect.Modifier.TRANSIENT;
import static java.util.Arrays.deepToString;
import static org.marid.misc.StringUtils.stripBrackets;

public class Reflections {

  @NotNull
  public static Stream<Class<?>> superclasses(@NotNull Class<?> type) {
    final var s = type.getSuperclass();
    return s == null ? Stream.of(type) : Stream.concat(Stream.of(type), superclasses(s));
  }

  public static Object get(@NotNull Field field, @Nullable Object target) {
    try {
      return field.get(target);
    } catch (ReflectiveOperationException x) {
      throw new IllegalStateException(x);
    }
  }

  public static boolean allowAccess(@NotNull Field field) {
    try {
      field.setAccessible(true);
      return true;
    } catch (InaccessibleObjectException | SecurityException x) {
      return false;
    }
  }

  public static Stream<Field> fields(@NotNull Object bean) {
    return superclasses(bean.getClass())
        .flatMap(c -> Stream.of(c.getDeclaredFields()))
        .filter(Reflections::allowAccess);
  }

  public static int compare(@NotNull Field f1, @NotNull Field f2) {
    final int c = f1.getDeclaringClass().getName().compareTo(f2.getDeclaringClass().getName());
    return c == 0 ? f1.getName().compareTo(f2.getName()) : c;
  }

  public static Object[] serializedValues(@NotNull Object bean) {
    return fields(bean)
        .filter(f -> (f.getModifiers() & (STATIC | TRANSIENT)) == 0)
        .sorted(Reflections::compare)
        .map(f -> get(f, bean))
        .toArray();
  }

  public static int hashCode(@Nullable Object bean) {
    if (bean == null) {
      return 0;
    } else {
      return Arrays.deepHashCode(serializedValues(bean));
    }
  }

  public static boolean equals(@Nullable Object v1, @Nullable Object v2) {
    if (v1 == v2) {
      return true;
    } else if (v1 == null || v2 == null) {
      return false;
    } else if (v1.getClass() != v2.getClass()) {
      return false;
    } else {
      return Arrays.deepEquals(serializedValues(v1), serializedValues(v2));
    }
  }

  public static String toString(@NotNull Object bean) {
    final var name = bean.getClass().getSimpleName();
    return fields(bean)
        .filter(f -> (f.getModifiers() & (STATIC | TRANSIENT)) == 0)
        .map(f -> f.getName() + "=" + stripBrackets(deepToString(new Object[] {get(f, bean)})))
        .collect(Collectors.joining(",", name + "(", ")"));
  }
}
