/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.logging.formatters;

import org.marid.l10n.L10n;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Dmitry Ovchinnikov
 */
public class DefaultFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        final StringWriter sw = new StringWriter(128)
                .append(Instant.ofEpochMilli(record.getMillis()).toString())
                .append(' ')
                .append(record.getLevel().toString())
                .append(' ')
                .append(record.getLoggerName())
                .append(' ')
                .append(L10n.m(record.getMessage(), record.getParameters()))
                .append(System.lineSeparator());
        if (record.getThrown() != null) {
            final PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
        }
        return sw.toString();
    }
}