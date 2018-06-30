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

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ListDao<I, E extends Elem<I>> {

  void save(Collection<? extends E> data);

  List<E> load();

  Set<I> getIds();
}
