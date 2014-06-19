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

package org.marid.bde.view;

import org.marid.bde.view.BlockView.In;
import org.marid.bde.view.BlockView.Out;
import org.marid.bde.view.ga.GaContext;
import org.marid.bde.view.ga.Specie;
import org.marid.collections.ImmutableArrayMap;
import org.marid.logging.LogSupport;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * @author Dmitry Ovchinnikov.
 */
public class BlockLink<S extends Specie<S>> implements LogSupport {

    private final S[] species;
    public final BlockView.In in;
    public final BlockView.Out out;
    private final IntFunction<S[]> iFunc;
    Incubator incubator;

    public BlockLink(int sCount, Function<BlockLink<S>, S> sFunc, IntFunction<S[]> iFunc, In in, Out out) {
        this.in = in;
        this.out = out;
        this.iFunc = iFunc;
        this.species = iFunc.apply(sCount);
        for (int i = 0; i < sCount; i++) {
            this.species[i] = sFunc.apply(this);
        }
    }

    public void paint(Graphics2D g) {
        species[0].paint(g);
    }

    public void initIncubator(int size) {
        incubator = new Incubator(size * species.length);
    }

    public S getSpecie() {
        return species[0];
    }

    public void doGA(GaContext gc) {
        incubator.count = 0;
        try {
            while (incubator.count < incubator.length) {
                final int n = gc.random.nextInt(1, species.length);
                final int mi = gc.random.nextInt(n);
                final int fi = gc.random.nextInt(n);
                final S male = species[mi];
                final S female = species[mi == fi ? fi + 1 : fi];
                final S child = male.crossover(gc, female);
                child.mutate(gc);
                incubator.put(child.fitness(gc), child);
            }
            incubator.copy();
        } catch (Exception x) {
            warning("GA error", x);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + new ImmutableArrayMap<>("in", in, "out", out, "specie", species[0]);
    }

    class Incubator {

        final double[] fitnesses;
        final S[] species;
        int count;
        int length;

        Incubator(int count) {
            fitnesses = new double[count];
            species = iFunc.apply(count);
            length = count;
        }

        void put(double fitness, S specie) {
            final int index = -(Arrays.binarySearch(fitnesses, 0, count, fitness) + 1);
            if (index < 0) {
                return;
            }
            System.arraycopy(fitnesses, index, fitnesses, index + 1, count - index);
            System.arraycopy(species, index, species, index + 1, count - index);
            fitnesses[index] = fitness;
            species[index] = specie;
            count++;
        }

        void copy() {
            System.arraycopy(species, 0, BlockLink.this.species, 0, BlockLink.this.species.length);
        }
    }
}