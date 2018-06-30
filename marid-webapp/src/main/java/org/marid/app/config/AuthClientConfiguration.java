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

import org.marid.app.props.FacebookAuthProperties;
import org.marid.app.props.GoogleAuthProperties;
import org.marid.app.props.TwitterAuthProperties;
import org.marid.app.props.WebProperties;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AuthClientConfiguration {

  @Bean
  public Google2Client google2Client(GoogleAuthProperties properties) {
    return new Google2Client(properties.getClientId(), properties.getSecret());
  }

  @Bean
  public FacebookClient facebookClient(FacebookAuthProperties properties) {
    final var client = new FacebookClient(properties.getClientId(), properties.getSecret());
    client.setScope("email");
    return client;
  }

  @Bean
  public TwitterClient twitterClient(TwitterAuthProperties properties) {
    final var client = new TwitterClient(properties.getClientId(), properties.getSecret());
    client.setIncludeEmail(true);
    client.setAlwaysConfirmAuthorization(true);
    return client;
  }

  @Bean
  public Clients authClients(Client<?, ?>[] clients, WebProperties properties) {
    final String callback = String.format("https://%s:%d/callback", properties.getHost(), properties.getPort());
    final Clients authClients = new Clients(callback, clients);
    authClients.addAuthorizationGenerator((context, profile) -> {
      profile.addRole("ROLE_USER");
      return profile;
    });
    return authClients;
  }

  @Bean
  public Config authConfig(Clients authClients) {
    final Config config = new Config(authClients);
    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
    config.addAuthorizer("user", new RequireAnyRoleAuthorizer<>("ROLE_USER"));
    return config;
  }
}
