/*-
 * #%L
 * marid-runtime
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

package org.marid.expression.generic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.marid.cellar.BottleContext;
import org.marid.types.MaridArrayType;
import org.marid.types.Types;

import java.lang.reflect.Type;
import java.util.List;

public interface ArrayExpression extends Expression {

  @NotNull
  List<? extends Expression> getElements();

  @NotNull
  @Override
  default Type getType(@Nullable Type owner, @NotNull BottleContext context) {
    return getElements().stream()
        .map(e -> e.getType(owner, context))
        .distinct()
        .reduce(Types::common)
        .map(MaridArrayType::array)
        .orElse(Object[].class);
  }
}
