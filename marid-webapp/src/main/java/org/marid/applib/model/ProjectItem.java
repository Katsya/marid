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
package org.marid.applib.model;

import org.jetbrains.annotations.NotNull;

public final class ProjectItem implements Id<String> {

  private final String id;

  public ProjectItem(String id) {
    this.id = id;
  }

  @NotNull
  @Override
  public String getId() {
    return id;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null || obj.getClass() != ProjectItem.class) {
      return false;
    } else {
      final var that = (ProjectItem) obj;
      return id.equals(that.id);
    }
  }

  @Override
  public String toString() {
    return id;
  }
}
