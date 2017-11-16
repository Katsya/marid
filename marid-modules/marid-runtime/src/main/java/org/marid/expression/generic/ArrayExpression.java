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

package org.marid.expression.generic;

import org.marid.types.TypeContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.reflect.TypeUtils.genericArrayType;

public interface ArrayExpression extends Expression {

  @Nonnull
  List<? extends Expression> getElements();

  @Nonnull
  @Override
  default Type getType(@Nullable Type owner, @Nonnull TypeContext context) {
    final List<Type> set = getElements().stream().map(e -> e.getType(owner, context)).distinct().collect(toList());
    final Type elementType = context.commonAncestor(Object.class, set);
    return elementType instanceof Class<?>
        ? Array.newInstance((Class<?>) elementType, 0).getClass()
        : genericArrayType(elementType);
  }
}
