/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.applib.dao;

import org.marid.applib.model.Elem;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static org.marid.applib.dao.ListStore.EventType.*;
import static org.marid.logging.Log.log;

public class ListStore<I, E extends Elem<I>, D extends ListDao<I, E>> {

  protected final D dao;
  protected final ArrayList<E> list = new ArrayList<>();
  protected final EnumMap<EventType, Collection<Consumer<Event>>> listeners = new EnumMap<>(EventType.class);

  public ListStore(D dao) {
    this.dao = dao;
  }

  public void refresh() {
    final var newList = dao.load();
    final var remove = new TreeMap<Integer, E>();

    for (int i = list.size() - 1; i >= 0; i--) {
      final var e = list.get(i);
      if (newList.stream().noneMatch(v -> v.getId().equals(e.getId()))) {
        list.remove(i);
        remove.put(i, e);
      }
    }

    if (!remove.isEmpty()) {
      listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(remove)::fire);
    }

    add(newList);
  }

  public void save() {
    dao.save(list);
  }

  protected int locateIndex(I key) {
    for (final var i = list.listIterator(); i.hasNext(); ) {
      final var e = i.next();
      if (e.getId().equals(key)) {
        return i.previousIndex();
      }
    }
    return -list.size() - 1;
  }

  public void add(List<E> added) {
    final var addOrdered = new LinkedList<Map.Entry<Integer, E>>();
    final var update = new TreeMap<Integer, E>();
    for (final var e : added) {
      final int index = locateIndex(e.getId());
      if (index < 0) {
        final int pos = -(index + 1);
        addOrdered.add(Map.entry(pos, e));
        list.add(pos, e);
      } else if (!e.equals(list.get(index))) {
        list.set(index, e);
        update.put(index, e);
      }
    }
    if (!addOrdered.isEmpty()) {
      final var add = addOrdered.stream().reduce(
          new TreeMap<Integer, E>(),
          (a, e) -> {
            if (a.isEmpty()) {
              a.put(e.getKey(), e.getValue());
            } else {
              if (e.getKey() > a.lastKey()) {
                a.put(e.getKey(), e.getValue());
              } else {
                listeners.getOrDefault(ADD, emptyList()).forEach(new Event(a)::fire);
                return new TreeMap<>(Map.ofEntries(e));
              }
            }
            return a;
          },
          (a1, a2) -> a2
      );
      listeners.getOrDefault(ADD, emptyList()).forEach(new Event(add)::fire);
    }

    if (!update.isEmpty()) {
      listeners.getOrDefault(UPDATE, emptyList()).forEach(new Event(update)::fire);
    }
  }

  public void remove(int... indices) {
    final TreeMap<Integer, E> remove = new TreeMap<>();
    for (final int i : indices) {
      if (i >= 0 && i < list.size()) {
        final var e = list.get(i);
        remove.put(i, e);
      }
    }
    if (!remove.isEmpty()) {
      remove.descendingKeySet().stream().mapToInt(Integer::intValue).forEach(list::remove);
      listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(remove)::fire);
    }
  }

  public void update(int... indices) {
    final TreeMap<Integer, E> update = new TreeMap<>();
    for (final int i : indices) {
      if (i >= 0 && i < list.size()) {
        final var e = list.get(i);
        update.put(i, e);
      }
    }
    if (!update.isEmpty()) {
      listeners.getOrDefault(UPDATE, emptyList()).forEach(new Event(update)::fire);
    }
  }

  public Consumer<Event> addListener(EventType type, Consumer<Event> listener) {
    listeners.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(listener);
    return listener;
  }

  public void removeListener(EventType type, Consumer<Event> listener) {
    listeners.computeIfPresent(type, (k, old) -> old.remove(listener) && old.isEmpty() ? null : old);
  }

  public E get(int index) {
    return list.get(index);
  }

  public boolean contains(I id) {
    return locateIndex(id) >= 0;
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public class Event {

    public final TreeMap<Integer, E> update;

    public Event(TreeMap<Integer, E> update) {
      this.update = update;
    }

    public ListStore<I, E, D> getSource() {
      return ListStore.this;
    }

    public void fire(Consumer<Event> listener) {
      try {
        listener.accept(this);
      } catch (Exception x) {
        log(WARNING, "Unable to invoke listener of {0}", x, this);
      } catch (Throwable x) {
        log(SEVERE, "Unexpected error of {0}", x, this);
      }
    }
  }

  public enum EventType {
    ADD,
    REMOVE,
    UPDATE
  }
}
