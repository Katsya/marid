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
package org.marid.applib.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.server.VaadinSession;
import org.jetbrains.annotations.PropertyKey;
import org.marid.l10n.L10n;

import java.util.Locale;
import java.util.Optional;

public interface Locales {

  static String m(ValueContext ctx, @PropertyKey(resourceBundle = "res.messages") String key, Object... args) {
    final var locale = ctx.getLocale()
        .or(() -> Optional.ofNullable(UI.getCurrent()).map(UI::getLocale))
        .or(() -> Optional.ofNullable(VaadinSession.getCurrent()).map(VaadinSession::getLocale))
        .orElseGet(Locale::getDefault);
    return L10n.m(locale, key, args);
  }

  static String m(@PropertyKey(resourceBundle = "res.messages") String key, Object... args) {
    final var session = VaadinSession.getCurrent();
    final var locale = session == null ? Locale.getDefault() : session.getLocale();
    return L10n.m(locale, key, args);
  }

  static String s(@PropertyKey(resourceBundle = "res.strings") String key, Object... args) {
    final var session = VaadinSession.getCurrent();
    final var locale = session == null ? Locale.getDefault() : session.getLocale();
    return L10n.s(locale, key, args);
  }
}
