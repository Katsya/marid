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
package org.marid.applib.validators;

import org.eclipse.jface.dialogs.IInputValidator;

import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.marid.applib.utils.Locales.m;

public interface InputValidators {

  static IInputValidator fileName() {
    return value -> {
      if (value == null) {
        return m("nullValue");
      }
      if (value.isEmpty()) {
        return m("emptyValue");
      }
      if (value.length() < 2) {
        return m("tooShortName");
      }
      if (value.chars().anyMatch(c -> !Character.isJavaIdentifierPart(c))) {
        return m("invalidName");
      }
      return null;
    };
  }

  static IInputValidator input(UnaryOperator<Optional<String>> predicate) {
    return value -> predicate.apply(Optional.ofNullable(value)).orElse(null);
  }

  static IInputValidator inputs(IInputValidator... validators) {
    return v -> Stream.of(validators)
        .map(validator -> validator.isValid(v))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }
}
