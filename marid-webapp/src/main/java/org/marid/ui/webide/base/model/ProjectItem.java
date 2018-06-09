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
package org.marid.ui.webide.base.model;

import org.jetbrains.annotations.NotNull;

public class ProjectItem implements Comparable<ProjectItem> {

  public final String name;
  public final long size;

  public ProjectItem(String name, long size) {
    this.name = name;
    this.size = size;
  }

  @Override
  public int compareTo(@NotNull ProjectItem o) {
    return name.compareTo(o.name);
  }
}
