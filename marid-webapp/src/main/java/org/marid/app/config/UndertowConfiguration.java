/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.app.config;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionManager;
import io.undertow.server.session.SslSessionConfig;
import org.marid.app.handlers.MainHandler;
import org.marid.app.http.BowerResourceManager;
import org.marid.app.http.MaridResourceManager;
import org.marid.app.http.NpmResourceManager;
import org.marid.app.props.UndertowProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;

@Component
public class UndertowConfiguration {

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Undertow undertow(SSLContext sslContext, UndertowProperties properties, HttpHandler rootHandler) {
    return Undertow.builder()
        .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
        .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH, true)
        .addListener(new Undertow.ListenerBuilder()
            .setType(Undertow.ListenerType.HTTPS)
            .setHost(properties.getHost())
            .setPort(properties.getPort())
            .setRootHandler(rootHandler)
            .setSslContext(sslContext)
        )
        .build();
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public SessionManager sessionManager(UndertowProperties properties) {
    final InMemorySessionManager sessionManager = new InMemorySessionManager("maridSessionManager");
    sessionManager.setDefaultSessionTimeout(properties.getSessionTimeout());
    return sessionManager;
  }

  @Bean
  public HttpHandler rootHandler(MainHandler mainHandler, SessionManager sessionManager) {
    final var sh = new SessionAttachmentHandler(mainHandler, sessionManager, new SslSessionConfig(sessionManager));
    return new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, null, sh);
  }

  @Bean
  public BowerResourceManager bowerResourceManager() {
    return new BowerResourceManager("bootstrap", "jquery");
  }

  @Bean
  public NpmResourceManager npmResourceManager() {
    return new NpmResourceManager("ionicons");
  }

  @Bean
  public ResourceManager pubResourceManager() {
    return new MaridResourceManager("/public/");
  }

  @Bean
  public ResourceManager userResourceManager(BowerResourceManager bower, NpmResourceManager npm) {
    return new MaridResourceManager("/user/").addParent(bower).addParent(npm);
  }

  @Bean
  public ResourceManager adminResourceManager(ResourceManager userResourceManager) {
    return new MaridResourceManager("/admin/").addParent(userResourceManager);
  }
}

