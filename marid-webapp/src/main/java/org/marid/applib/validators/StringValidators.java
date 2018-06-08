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
package org.marid.applib.validators;


import com.vaadin.flow.data.binder.Validator;

import java.io.File;

import static com.vaadin.flow.data.binder.ValidationResult.error;
import static com.vaadin.flow.data.binder.ValidationResult.ok;
import static org.marid.applib.utils.Locales.m;

public interface StringValidators {

  static Validator<String> fileNameValidator() {
    return (c, ctx) -> {
      if (c.isEmpty()) {
        return error(m(ctx, "fileNameIsEmpty"));
      }

      if (c.startsWith(".")) {
        return error(m(ctx, "fileNameStartsWithDot"));
      }

      if (c.contains(File.separator) || c.contains("/") || c.contains("..")) {
        return error(m(ctx, "fileNameContainsPathCharacters"));
      }

      return ok();
    };
  }
}
