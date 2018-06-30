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

import java.util.Comparator;

public class SortedListStore<I, T extends Elem<I>, D extends ListDao<I, T>> extends ListStore<I, T, D> {

  private final Comparator<? super I> comparator;

  public SortedListStore(D dao, Comparator<? super I> comparator) {
    super(dao);
    this.comparator = comparator;
  }

  @Override
  protected int locateIndex(I key) {
    int low = 0;
    int high = list.size() - 1;

    while (low <= high) {
      final int mid = (low + high) >>> 1;
      final T midVal = list.get(mid);
      final int cmp = comparator.compare(midVal.getId(), key);

      if (cmp < 0) {
        low = mid + 1;
      } else if (cmp > 0) {
        high = mid - 1;
      } else {
        return mid;
      }
    }

    return -(low + 1);
  }
}
