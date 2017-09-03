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

package org.marid.runtime.context;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.marid.runtime.beans.Bean;
import org.marid.runtime.beans.BeanMethod;
import org.marid.runtime.exception.MaridBeanNotFoundException;
import org.marid.test.NormalTests;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.marid.runtime.context.MaridContextTestUtils.m;

/**
 * @author Dmitry Ovchinnikov
 */
@Category({NormalTests.class})
public class MaridContextTest {

    @Test
    public void simple() throws Exception {
        final Bean root = new Bean();
        root.add(
                new Bean("bean2", "bean1", Bean1.class.getMethod("getZ")),
                new Bean(
                        "bean1",
                        Bean1.class.getName(),
                        Bean1.class.getConstructor(int.class, String.class, BigDecimal.class),
                        m("x", "of", "1"),
                        m("y", "of", "abc"),
                        m("z", "of", "1.23")
                ).add(new BeanMethod(Bean1.class.getMethod("setA", boolean.class), m("a", "of", "true"))),
                new Bean("bean3", "bean1", Bean1.class.getField("y")),
                new Bean(
                        "bean4",
                        Bean1.class.getName(),
                        Bean1.class.getMethod("list")
                ).add(new BeanMethod(List.class.getMethod("add", Object.class), m("e", "Integer", "1"))),
                new Bean(
                        "bean5",
                        Bean1.class.getName(),
                        Bean1.class.getMethod("list")
                ).add(
                        new BeanMethod(List.class.getMethod("add", Object.class), m("e", "Integer", "1")),
                        new BeanMethod(List.class.getMethod("add", Object.class), m("e", "Integer", "2"))),
                new Bean(
                        "bean6",
                        String.class.getName(),
                        String.class.getMethod("valueOf", Object.class), m("arg0", "js", "'a' + 1")
                ),
                new Bean(
                        "bean7",
                        String.class.getName(),
                        String.class.getConstructor(String.class),
                        m("arg", "ref", "bean8")
                ),
                new Bean(
                        "bean8",
                        String.class.getName(),
                        String.class.getConstructor(String.class),
                        m("arg", "of", "x")
                )
        );
        try (final MaridContext runtime = new MaridContext(root)) {
            assertEquals("bean1", new Bean1(1, "abc", new BigDecimal("1.23")).setA(true), runtime.getBean("bean1"));
            assertEquals("bean2", new BigDecimal("1.23"), runtime.getBean("bean2"));
            assertEquals("bean3", "abc", runtime.getBean("bean3"));
            assertEquals("bean4", singletonList(1), runtime.getBean("bean4"));
            assertEquals("bean5", asList(1, 2), runtime.getBean("bean5"));
            assertEquals("bean6", "a1", runtime.getBean("bean6"));
            assertEquals("bean1", 10, ((Bean1) runtime.getBean("bean1")).q);
            assertEquals("bean7", "x", runtime.getBean("bean7"));
        }
    }

    @Test(expected = MaridBeanNotFoundException.class)
    public void circularReferenceDetection() throws Throwable {
        final Bean root = new Bean();
        root.add(new Bean(
                "bean1",
                Bean1.class.getName(),
                Bean1.class.getConstructor(int.class, String.class, BigDecimal.class),
                m("x", "of", "1"), m("y", "ref", "bean1"), m("z", "of", "1.23")
        ).add(new BeanMethod(
                Bean1.class.getMethod("setA", boolean.class),
                m("a", "boolean", "true")
        )));
        try (final MaridContext context = new MaridContext(root)) {
            assertNull(context);
        } catch (MaridContextException x) {
            throw x.getSuppressed()[0];
        }
    }
}
