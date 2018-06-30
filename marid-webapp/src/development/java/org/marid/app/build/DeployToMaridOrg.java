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

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class DeployToMaridOrg {

  public static void main(String... args) throws Throwable {
    {
      final Process process = new ProcessBuilder(
          "mvn",
          "-DskipTests",
          "-pl", "org.marid:marid-webapp",
          "-P", "release",
          "-am",
          "clean", "install"
      ).inheritIO().start();
      final int result = process.waitFor();
      if (result != 0) {
        throw new IllegalStateException("Result: " + result);
      }
    }

    {
      final ProtectionDomain protectionDomain = DeployToMaridOrg.class.getProtectionDomain();
      final CodeSource codeSource = protectionDomain.getCodeSource();
      final URL location = codeSource.getLocation();
      final Path path = Paths.get(location.toURI()).getParent().getParent();

      final Process process = new ProcessBuilder("mvn", "wagon:sshexec@stop", "wagon:upload@deploy", "wagon:sshexec@start")
          .inheritIO()
          .directory(path.toFile())
          .start();
      final int result = process.waitFor();
      if (result != 0) {
        throw new IllegalStateException("Result: " + result);
      }
    }

    {
      final Process process = new ProcessBuilder(
          "mvn",
          "-DskipTests",
          "-pl", "org.marid:marid-webapp",
          "-P", "development",
          "-am",
          "clean", "install"
      ).inheritIO().start();
      final int result = process.waitFor();
      if (result != 0) {
        throw new IllegalStateException("Result: " + result);
      }
    }
  }
}
