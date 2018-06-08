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
package org.marid.app.common;

import com.vaadin.flow.i18n.I18NProvider;
import org.marid.l10n.L10n;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class InternationalizationProvider implements I18NProvider {

  @Override
  public List<Locale> getProvidedLocales() {
    return List.of(Locale.US);
  }

  @Override
  public String getTranslation(String key, Locale locale, Object... params) {
    return L10n.s(locale, key, params);
  }
}
