/*-
 * #%L
 * marid-spring
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
package org.marid.spring.annotation;

import org.marid.logging.Log;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.asm.ClassReader.SKIP_FRAMES;
import static org.springframework.asm.SpringAsmInfo.ASM_VERSION;
import static org.springframework.util.ClassUtils.getClassFileName;

public final class OrderedAnnotatedMethodProvider extends ClassValue<Method[]> {

  private final Class<? extends Annotation>[] annotations;

  @SafeVarargs
  public OrderedAnnotatedMethodProvider(Class<? extends Annotation>... annotations) {
    this.annotations = annotations;
  }

  @Override
  protected Method[] computeValue(Class<?> type) {
    final Method[] methods = Stream.of(type.getMethods())
        .filter(m -> Stream.of(annotations).anyMatch(m::isAnnotationPresent))
        .toArray(Method[]::new);
    if (methods.length == 0) {
      return null;
    }

    final var maxClass = new AtomicInteger();
    final var maxMethod = new AtomicInteger();
    final var maxLine = new AtomicInteger("unknown".length());

    final IdentityHashMap<Class<?>, LinkedHashMap<String, Integer>> linesMap = Stream.of(methods)
        .peek(m -> {
          final var className = m.getDeclaringClass().getName().length();
          final var name = m.getName().length();
          maxClass.updateAndGet(v -> className > v ? className : v);
          maxMethod.updateAndGet(v -> name > v ? name : v);
        })
        .map(Method::getDeclaringClass)
        .distinct()
        .collect(Collectors.toMap(c -> c, c -> {
          final var methodNames = Stream.of(methods)
              .filter(m -> m.getDeclaringClass() == c)
              .map(Method::getName)
              .collect(Collectors.toUnmodifiableSet());
          final var map = new LinkedHashMap<String, Integer>(methodNames.size());
          try (final var is = c.getResourceAsStream(getClassFileName(c))) {
            new ClassReader(is).accept(new ClassVisitor(ASM_VERSION) {
              @Override
              public MethodVisitor visitMethod(int acc, String name, String desc, String signature, String[] xs) {
                if (!methodNames.contains(name)) {
                  return null;
                }
                return new MethodVisitor(ASM_VERSION) {
                  @Override
                  public void visitLineNumber(int line, Label start) {
                    map.computeIfAbsent(name, k -> {
                      final int len = Integer.toString(line).length();
                      maxLine.updateAndGet(v -> len > v ? len : v);
                      return line;
                    });
                  }
                };
              }
            }, SKIP_FRAMES);
          } catch (IOException x) {
            throw new UncheckedIOException(x);
          }
          return map;
        }, (v1, v2) -> v2, IdentityHashMap::new));

    final Comparator<Method> comparator = (m1, m2) -> {
      if (m1.getDeclaringClass() == m2.getDeclaringClass()) {
        final var map = linesMap.get(m1.getDeclaringClass());
        final var l1 = map.getOrDefault(m1.getName(), Integer.MAX_VALUE);
        final var l2 = map.getOrDefault(m2.getName(), Integer.MAX_VALUE);
        return l1.compareTo(l2);
      } else if (m1.getDeclaringClass().isAssignableFrom(m2.getDeclaringClass())) {
        return -1;
      } else {
        return 1;
      }
    };
    Arrays.sort(methods, comparator);

    final String format = String.format("| %%-%ss | %%-%ss | %%%ss |%n", maxClass, maxMethod, maxLine);

    final var infoBuilder = new StringBuilder();
    final var line = new char[maxClass.get() + maxMethod.get() + maxLine.get() + 10];
    Arrays.fill(line, '-');
    try (final var formatter = new Formatter(infoBuilder)) {
      formatter.format("%n");
      formatter.format("%s%n", String.valueOf(line));

      for (final var method : methods) {
        final var className = method.getDeclaringClass().getName();
        final var name = method.getName();
        final var lineNumber = Optional.ofNullable(linesMap.get(method.getDeclaringClass()).get(name))
            .map(Object::toString)
            .orElse("unknown");

        formatter.format(format, className, name, lineNumber);
      }
      formatter.format("%s%n", String.valueOf(line));
    }

    Log.log(Logger.getLogger(type.getName()), Level.INFO, "Method order: {0}", infoBuilder);

    return methods;
  }
}
