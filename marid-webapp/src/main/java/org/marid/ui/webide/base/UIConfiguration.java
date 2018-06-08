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

import com.vaadin.flow.server.VaadinSession;
import org.marid.spring.annotation.MaridComponent;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Locale;

@MaridComponent
public class UIConfiguration {

  @Bean
  public Locale locale(VaadinSession session) {
    return session.getLocale();
  }

  @Bean
  public CommonProfile userProfile(VaadinSession session) {
    final LinkedHashMap map = (LinkedHashMap) session.getSession().getAttribute(Pac4jConstants.USER_PROFILES);
    return (CommonProfile) map.values().iterator().next();
  }
}
