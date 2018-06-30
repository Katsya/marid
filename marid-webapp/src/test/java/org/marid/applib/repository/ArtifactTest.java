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
package org.marid.applib.repository;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("normal")
class ArtifactTest {

  @Test
  void checkParameters() {
    final var parameters = Artifact.class.getConstructors()[0].getParameters();
    final var names = Stream.of(parameters).map(Parameter::getName).collect(toUnmodifiableList());

    assertTrue(names.contains("groupId"));
  }
}
