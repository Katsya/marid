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

import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
public class IvyCommonConfiguration {

  @Bean
  public IBiblioResolver iBiblioResolver() {
    final IBiblioResolver resolver = new IBiblioResolver();
    resolver.setM2compatible(true);
    resolver.setUsepoms(true);
    resolver.setName("central");
    return resolver;
  }

  @Bean
  @Conditional({M2RepositoryExists.class})
  public FileSystemResolver localMavenResolver() {
    final FileSystemResolver resolver = new FileSystemResolver();
    resolver.setLocal(true);
    resolver.setM2compatible(true);
    resolver.setName("local");
    return resolver;
  }
}
