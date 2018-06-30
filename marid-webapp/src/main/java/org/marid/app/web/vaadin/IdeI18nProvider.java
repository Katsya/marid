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
package org.marid.app.web.vaadin;

import com.vaadin.flow.i18n.I18NProvider;
import org.marid.l10n.L10n;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle.Control;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;

@Component
public class IdeI18nProvider implements I18NProvider {

  private final List<Locale> locales;

  public IdeI18nProvider() {
    final var control = Control.getControl(FORMAT_PROPERTIES);
    locales = Stream.of(Locale.getAvailableLocales())
        .filter(locale -> {
          final var bundleName = control.toBundleName("res.messages", locale);
          final var resourceName = control.toResourceName(bundleName, "properties");
          return Thread.currentThread().getContextClassLoader().getResource(resourceName) != null;
        })
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public List<Locale> getProvidedLocales() {
    return locales;
  }

  @Override
  public String getTranslation(String key, Locale locale, Object... params) {
    return L10n.m(locale, key, params);
  }
}
