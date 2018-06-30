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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Condition {

  boolean isTrue();

  default boolean isFalse() {
    return !isTrue();
  }

  Consumer<Boolean> addListener(Consumer<Boolean> listener);

  void removeListener(Consumer<Boolean> listener);

  default Condition not() {
    return (NotCondition) () -> this;
  }

  static Condition not(Condition condition) {
    return (NotCondition) () -> condition;
  }

  default Condition and(Condition condition) {
    final Condition[] conditions = {this, condition};
    return (AndCondition) () -> conditions;
  }

  static Condition and(Condition... conditions) {
    return (AndCondition) () -> conditions;
  }

  default Condition or(Condition condition) {
    final Condition[] conditions = {this, condition};
    return (OrCondition) () -> conditions;
  }

  static Condition or(Condition... conditions) {
    return (OrCondition) () -> conditions;
  }
}

class ValueCondition implements Condition {

  private final ConcurrentLinkedQueue<Consumer<Boolean>> listeners = new ConcurrentLinkedQueue<>();

  private boolean condition;

  <V> ValueCondition(ListenableValue<V> value, Predicate<V> predicate) {
    condition = predicate.test(value.get());
    value.addListener((o, n) -> setCondition(predicate.test(value.get())));
  }

  private void setCondition(boolean condition) {
    if (this.condition != condition) {
      this.condition = condition;
      listeners.forEach(l -> l.accept(condition));
    }
  }

  public boolean isTrue() {
    return condition;
  }

  public Consumer<Boolean> addListener(Consumer<Boolean> listener) {
    listeners.add(listener);
    return listener;
  }

  public void removeListener(Consumer<Boolean> listener) {
    listeners.removeIf(listener::equals);
  }
}

@FunctionalInterface
interface NotCondition extends Condition {

  Condition condition();

  @Override
  default boolean isTrue() {
    return condition().isFalse();
  }

  default boolean isFalse() {
    return condition().isTrue();
  }

  @Override
  default Consumer<Boolean> addListener(Consumer<Boolean> listener) {
    return condition().addListener(v -> listener.accept(!v));
  }

  @Override
  default void removeListener(Consumer<Boolean> listener) {
    condition().removeListener(listener);
  }
}

interface ArrayCondition extends Condition {

  Condition[] conditions();

  @Override
  default Consumer<Boolean> addListener(Consumer<Boolean> listener) {
    final AtomicBoolean value = new AtomicBoolean(isTrue());
    final Consumer<Boolean> consumer = v -> {
      final boolean newValue = isTrue();
      if (value.compareAndSet(!newValue, newValue)) {
        listener.accept(newValue);
      }
    };
    for (final var condition : conditions()) {
      condition.addListener(consumer);
    }
    return consumer;
  }

  @Override
  default void removeListener(Consumer<Boolean> listener) {
    for (final var condition : conditions()) {
      condition.removeListener(listener);
    }
  }
}

@FunctionalInterface
interface AndCondition extends ArrayCondition {

  Condition[] conditions();

  @Override
  default boolean isTrue() {
    for (final var condition : conditions()) {
      if (condition.isFalse()) {
        return false;
      }
    }
    return true;
  }

  @Override
  default boolean isFalse() {
    for (final var condition : conditions()) {
      if (condition.isFalse()) {
        return true;
      }
    }
    return false;
  }
}

@FunctionalInterface
interface OrCondition extends ArrayCondition {

  Condition[] conditions();

  @Override
  default boolean isTrue() {
    for (final var condition : conditions()) {
      if (condition.isTrue()) {
        return true;
      }
    }
    return false;
  }

  @Override
  default boolean isFalse() {
    for (final var condition : conditions()) {
      if (condition.isTrue()) {
        return false;
      }
    }
    return true;
  }
}
