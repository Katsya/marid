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

package org.marid;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;

/**
 * @author Dmitry Ovchinnikov
 */
public class StringStringHashMapBenchmark extends AbstractMapBenchmark<String, String> {

    public StringStringHashMapBenchmark() {
        super(new HashMap<>());
    }

    @Override
    protected String key(int index) {
        return String.valueOf(index);
    }

    @Override
    protected String value(int index) {
        return Integer.toBinaryString(index);
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(StringStringHashMapBenchmark.class.getSimpleName())
                .forks(1)
                .build()
        ).run();
    }
}