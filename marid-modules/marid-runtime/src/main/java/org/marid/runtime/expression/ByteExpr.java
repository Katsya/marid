/*-
 * #%L
 * marid-runtime
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

package org.marid.runtime.expression;

import javax.annotation.Nonnull;

public class ByteExpr extends ValueExpr implements ByteExpression {

    public ByteExpr(@Nonnull String value) {
        super(value);
    }

    public ByteExpr() {
        super("0");
    }

    @Override
    protected Object parseSubstituted(@Nonnull String substituted) {
        return Byte.decode(substituted);
    }
}
