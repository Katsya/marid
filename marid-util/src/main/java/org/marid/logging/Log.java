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

package org.marid.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

/**
 * @author Dmitry Ovchinnikov
 */
public class Log {

  private static final ClassValue<Logger> LOGGERS = new ClassValue<>() {
    @Override
    protected Logger computeValue(Class<?> type) {
      final String name = type.getName();
      final int index = name.indexOf('$');
      return index < 0 ? Logger.getLogger(name) : Logger.getLogger(name.substring(0, index));
    }
  };
  public static final StackWalker WALKER = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);

  public static void log(@NotNull Level level, @NotNull String message, @Nullable Throwable thrown, Object... args) {
    log(LOGGERS.get(WALKER.getCallerClass()), level, message, thrown, args);
  }

  public static void log(@NotNull Level level, @NotNull String message, Object... args) {
    log(LOGGERS.get(WALKER.getCallerClass()), level, message, args);
  }

  public static void log(@NotNull Logger logger, @NotNull Level level, @NotNull String message, Object... args) {
    log(logger, level, message, null, args);
  }

  public static void log(@NotNull Logger logger,
                         @NotNull Level level,
                         @NotNull String message,
                         @Nullable Throwable thrown,
                         Object... args) {
    final LogRecord record = new LogRecord(level, message);
    record.setLoggerName(logger.getName());
    record.setSourceClassName(null);
    record.setThrown(thrown);
    record.setParameters(args);
    logger.log(record);
  }
}
