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
package org.marid.applib.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Controls {

  static Stream<? extends Control> controls(Control control) {
    if (control instanceof Composite) {
      return Stream.concat(
          Stream.of(control),
          Stream.of(((Composite) control).getChildren()).flatMap(Controls::controls)
      );
    } else {
      return Stream.of(control);
    }
  }

  static <C extends Control> Stream<? extends C> controls(Control control, Class<C> type) {
    return controls(control).filter(type::isInstance).map(type::cast);
  }

  static <C extends Control> Optional<? extends C> control(Composite composite, Class<C> type, Predicate<C> filter) {
    return controls(composite, type).filter(filter).findFirst();
  }

  static <C extends Control> Optional<? extends C> control(Composite composite, Class<C> type, Object data) {
    return control(composite, type, c -> data.equals(c.getData()));
  }
}
