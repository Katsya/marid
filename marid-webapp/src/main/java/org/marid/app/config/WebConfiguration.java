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

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private final Logger logger;

  public WebConfiguration(Logger logger) {
    this.logger = logger;
  }

  @Bean
  public LocaleResolver localeResolver() {
    final SessionLocaleResolver resolver = new SessionLocaleResolver();
    resolver.setDefaultLocale(Locale.UK);
    return resolver;
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ErrorViewResolver maridErrorViewResolver() {
    return (request, status, model) -> {
      System.out.println(model);
      return null;
    };
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    final LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    interceptor.setParamName("l");
    registry.addInterceptor(interceptor);
  }
}
