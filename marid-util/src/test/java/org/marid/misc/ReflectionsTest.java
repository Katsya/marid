/*-
 * #%L
 * marid-util
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
package org.marid.misc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.marid.misc.Builder.build;

@Tag("normal")
class ReflectionsTest {

  private int x;
  private short[] y;
  private String[] z;

  @Test
  void testHashCode() {
    x = 1;
    y = new short[]{1, 2};
    z = new String[]{"a", "b"};

    final int expected = Arrays.deepHashCode(new Object[]{x, y, z});
    final int actual = Reflections.hashCode(this);

    assertEquals(expected, actual);
  }

  static Object[][] equalsData() {
    return new Object[][]{
        {
            build(new ReflectionsTest(), t -> t.x = 1, t -> t.y = new short[]{1}, t -> t.z = new String[]{"p"}),
            build(new ReflectionsTest(), t -> t.x = 1, t -> t.y = new short[]{1}, t -> t.z = new String[]{"p"}),
            true
        },
        {
            build(new ReflectionsTest(), t -> t.x = 1, t -> t.y = new short[]{1}, t -> t.z = new String[]{"p"}),
            build(new ReflectionsTest(), t -> t.x = 1, t -> t.y = new short[]{1}, t -> t.z = new String[]{"a"}),
            false
        }
    };
  }

  @ParameterizedTest
  @MethodSource("equalsData")
  void testEquals(ReflectionsTest v1, ReflectionsTest v2, boolean equals) {
    assertEquals(Reflections.equals(v1, v2), equals);
  }
}
