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

package org.marid.app.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SslProperties {

  @Value("${ssl.password:123456}")
  private String password;

  public String getPassword() {
    return password;
  }

  public SslProperties setPassword(String password) {
    this.password = password;
    return this;
  }

  @Override
  public String toString() {
    return String.format("%s(%s)", getClass().getSimpleName(), password);
  }
}
