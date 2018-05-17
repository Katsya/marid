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
package org.marid.ui.webide.base;

import com.vaadin.server.VaadinSession;
import org.marid.app.annotation.PrototypeScoped;
import org.marid.applib.l10n.Msgs;
import org.marid.applib.l10n.Strs;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UIConfiguration {

  @Bean
  @PrototypeScoped
  public Locale locale(VaadinSession session) {
    return session.getLocale();
  }

  @Bean
  @PrototypeScoped
  public Strs strs(ObjectFactory<Locale> locale) {
    return new Strs(locale.getObject());
  }

  @Bean
  @PrototypeScoped
  public Msgs msgs(ObjectFactory<Locale> locale) {
    return new Msgs(locale.getObject());
  }
}
