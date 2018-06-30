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
package org.marid.ui.ide;

import com.vaadin.flow.server.VaadinSession;
import org.jetbrains.annotations.PropertyKey;
import org.marid.l10n.L10n;

import java.util.Locale;

public interface I18N {

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
