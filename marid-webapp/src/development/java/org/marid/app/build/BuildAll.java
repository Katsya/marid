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

package org.marid.app.build;

public class BuildAll {

  public static void main(String... args) throws Throwable {
    final Process process = new ProcessBuilder(
        "mvn",
        "-B", "-T8",
        "-DskipTests",
        "-Pdevelopment",
        "clean", "install"
    )
        .inheritIO()
        .start();
    final int result = process.waitFor();
    System.out.println(result);
  }
}
