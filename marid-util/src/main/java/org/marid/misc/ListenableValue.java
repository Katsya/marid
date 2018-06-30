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

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.deepEquals;

public class ListenableValue<V> {

  private final ConcurrentLinkedQueue<BiConsumer<V, V>> listeners = new ConcurrentLinkedQueue<>();

  private V value;

  public ListenableValue() {
  }

  public ListenableValue(V value) {
    this.value = value;
  }

  public void set(V v) {
    final V o = value;
    if (o != v) {
      if (o == null || v == null) {
        this.value = v;
        listeners.forEach(l -> l.accept(o, v));
      } else if (v.getClass().isArray() && o.getClass().isArray()) {
        if (!deepEquals(new Object[]{o}, new Object[]{v})) {
          this.value = v;
          listeners.forEach(l -> l.accept(o, v));
        }
      } else {
        if (!o.equals(v)) {
          this.value = v;
          listeners.forEach(l -> l.accept(o, v));
        }
      }
    }
  }

  public V get() {
    return value;
  }

  public boolean isEmpty() {
    return value == null;
  }

  public Optional<V> value() {
    return Optional.ofNullable(value);
  }

  public BiConsumer<V, V> addListener(BiConsumer<V, V> listener) {
    listeners.add(listener);
    return listener;
  }

  public boolean removeListener(BiConsumer<V, V> listener) {
    return listeners.removeIf(listener::equals);
  }

  public Condition condition(Predicate<V> predicate) {
    return new ValueCondition(this, predicate);
  }

  @SafeVarargs
  public static <V> Condition[] conditions(Predicate<V> predicate, ListenableValue<V>... values) {
    return Stream.of(values).map(v -> v.condition(predicate)).toArray(Condition[]::new);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
