/*-
 * #%L
 * marid-util
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
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

package org.marid.misc;

import org.jetbrains.annotations.NotNull;
import org.marid.logging.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * @author Dmitry Ovchinnikov
 */
public interface StringUtils {

  static Predicate<Path> pathEndsWith(String suffix) {
    return p -> p.getFileName().toString().endsWith(suffix);
  }

  static String throwableText(Throwable throwable) {
    final StringWriter writer = new StringWriter();
    try (final PrintWriter w = new PrintWriter(writer)) {
      throwable.printStackTrace(w);
    }
    return writer.toString();
  }

  static int count(String string, char c) {
    int count = 0, i = -1;
    while ((i = string.indexOf(c, i + 1)) >= 0) {
      count++;
    }
    return count;
  }

  static String stringOrNull(Object object) {
    return object == null ? null : object.toString();
  }

  static String sizeBinary(Locale locale, long size, int precision) {
    final String[] suffices = {"", " KiB", " MiB", " GiB", " TiB", " PiB", " EiB"};
    final int index = (64 - Long.numberOfLeadingZeros(size)) / 10;
    final long divider = 1 << (index * 10);
    final float value = size / (float) divider;

    final var format = NumberFormat.getNumberInstance(locale);
    format.setMaximumFractionDigits(precision);
    return format.format(value) + suffices[index];
  }

  static String stripBrackets(@NotNull String value) {
    if (value.startsWith("[") && value.endsWith("]")) {
      return value.substring(1, value.length() - 1);
    } else {
      return value;
    }
  }

  static String resource(String file) {
    final var caller =  Log.WALKER.getCallerClass();
    try (final var stream = caller.getResourceAsStream(file)) {
      final var out = new ByteArrayOutputStream();
      stream.transferTo(out);
      return out.toString(StandardCharsets.UTF_8);
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }
}
