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

package org.marid.app.ivy;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class M2RepositoryExists implements Condition {

  @Override
  public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
    final Path path = Paths.get(System.getProperty("user.home"), ".m2", "repository");
    return Files.isDirectory(path);
  }
}
