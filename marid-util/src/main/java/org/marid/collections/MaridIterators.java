/*-
 * #%L
 * marid-util
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

package org.marid.collections;

import org.jetbrains.annotations.NotNull;
import org.marid.misc.Casts;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface MaridIterators {

  @NotNull
  static <E> Iterator<E> iterator(@NotNull BooleanSupplier hasNext, @NotNull Supplier<E> next) {
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return hasNext.getAsBoolean();
      }

      @Override
      public E next() {
        return next.get();
      }
    };
  }

  static <E> Stream<E> stream(@NotNull Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  @NotNull
  static Iterator<String> lineIterator(@NotNull Scanner scanner) {
    return iterator(scanner::hasNextLine, scanner::nextLine);
  }

  static <E> void forEach(@NotNull Iterable<E> iterable, @NotNull BiConsumer<Boolean, E> consumer) {
    boolean hasPrevious = false;
    for (final E e : iterable) {
      consumer.accept(hasPrevious, e);
      if (!hasPrevious) {
        hasPrevious = true;
      }
    }
  }

  @NotNull
  static <E> Iterable<E> iterable(@NotNull Supplier<@NotNull Iterator<E>> iteratorSupplier) {
    return iteratorSupplier::get;
  }

  @NotNull
  static <E> Iterable<E> iterable(@NotNull Iterator<E> iterator) {
    return () -> iterator;
  }

  @NotNull
  static <T> T[] array(@NotNull Class<T> type, @NotNull Iterable<? extends T> iterable) {
    final LinkedList<T> list = new LinkedList<>();
    iterable.forEach(list::add);
    return list.toArray(Casts.cast(Array.newInstance(type, list.size())));
  }

  @NotNull
  static <T> T[] array(@NotNull Class<T> type, @NotNull Iterator<? extends T> iterator) {
    final LinkedList<T> list = new LinkedList<>();
    iterator.forEachRemaining(list::add);
    return list.toArray(Casts.cast(Array.newInstance(type, list.size())));
  }
}
