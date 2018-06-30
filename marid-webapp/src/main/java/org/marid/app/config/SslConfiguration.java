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

package org.marid.app.config;

import org.marid.app.props.SslProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

@Component
public class SslConfiguration {

  @Bean
  public KeyStore keyStore(SslProperties properties) throws Exception {
    final var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    try (final var inputStream = getClass().getResourceAsStream("/marid.jks")) {
      keyStore.load(inputStream, properties.getPassword().toCharArray());
    }
    return keyStore;
  }

  @Bean
  public KeyManagerFactory keyManagerFactory(KeyStore keyStore, SslProperties properties) throws Exception {
    final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(keyStore, properties.getPassword().toCharArray());
    return kmf;
  }

  @Bean
  public TrustManagerFactory trustManagerFactory(KeyStore keyStore) throws Exception {
    final var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(keyStore);
    return tmf;
  }

  @Bean
  public SSLContext sslContext(KeyManagerFactory kmf, TrustManagerFactory tmf) throws Exception {
    final SSLContext context = SSLContext.getInstance("TLS");
    context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    return context;
  }
}
