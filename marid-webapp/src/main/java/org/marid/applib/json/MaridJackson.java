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
package org.marid.applib.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.TimeZone;

public interface MaridJackson {

  ObjectMapper MAPPER = new ObjectMapper()
      .setLocale(Locale.GERMANY)
      .setTimeZone(TimeZone.getDefault())
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .findAndRegisterModules();
}
