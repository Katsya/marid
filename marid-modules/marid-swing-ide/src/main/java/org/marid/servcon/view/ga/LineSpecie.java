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

package org.marid.servcon.view.ga;

import org.marid.logging.LogSupport;
import org.marid.servcon.view.BlockLink;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;

/**
 * @author Dmitry Ovchinnikov.
 */
public class LineSpecie extends Specie<LineSpecie> implements LogSupport {

    private static final int BORDER = 20;
    private static final int COUNT = 5;

    private final int[] xs;
    private final int[] ys;

    public LineSpecie(BlockLink<LineSpecie> blockLink) {
        super(blockLink);
        this.xs = new int[COUNT];
        this.ys = new int[COUNT];
        final Point p1 = blockLink.out.connectionPoint();
        final Point p2 = blockLink.in.connectionPoint();
        final int dx = (p2.x - p1.x) / COUNT;
        final int dy = (p2.y - p1.y) / COUNT;
        for (int i = 0; i < COUNT; i++) {
            xs[i] = p1.x + dx * (i + 1);
            ys[i] = p1.y + dy * (i + 1);
        }
    }

    private LineSpecie(BlockLink<LineSpecie> blockLink, int[] xs, int[] ys) {
        super(blockLink);
        this.xs = xs;
        this.ys = ys;
    }

    private double length(GaContext fc) {
        double length = Point.distance(fc.p1.x + BORDER, fc.p1.y, xs[0], ys[0]);
        for (int i = 0; i < COUNT - 1; i++) {
            length += Point.distance(xs[i], ys[i], xs[i + 1], ys[i + 1]);
        }
        length += Point.distance(xs[COUNT - 1], ys[COUNT - 1], fc.p2.x - BORDER, fc.p2.y);
        return length;
    }

    @Override
    public void paint(Graphics2D g) {
        final Point p1 = blockLink.out.connectionPoint();
        final Point p2 = blockLink.in.connectionPoint();
        g.drawLine(p1.x, p1.y, p1.x + BORDER, p1.y);
        g.drawLine(p1.x + BORDER, p1.y, xs[0], ys[0]);
        g.drawPolyline(xs, ys, COUNT);
        g.drawLine(xs[COUNT - 1], ys[COUNT - 1], p2.x - BORDER, p2.y);
        g.drawLine(p2.x - BORDER, p2.y, p2.x, p2.y);
    }

    @Override
    public double fitness(GaContext fc) {
        try {
            final double lineDistance = Point.distance(fc.p1.x + BORDER, fc.p1.y, fc.p2.x - BORDER, fc.p2.y) + 0.1;
            final double distFactor = length(fc) / lineDistance;
            double isectFactor = 0.0;
            for (final Rectangle r : fc.rectangles) {
                final double cx = r.getCenterX();
                final double cy = r.getCenterY();
                final double rr = Point.distance(cx, cy, r.getMinY(), r.getMinY()) * 2;
                isectFactor += isectF(r, cx, cy, rr, fc.p1.x + BORDER, fc.p1.y, xs[0], ys[0]);
                for (int i = 0; i < COUNT - 1; i++) {
                    isectFactor += isectF(r, cx, cy, rr, xs[i], ys[i], xs[i + 1], ys[i + 1]);
                }
                isectFactor += isectF(r, cx, cy, rr, xs[COUNT - 1], ys[COUNT - 1], fc.p2.x - BORDER, fc.p2.y);
            }
            return distFactor + isectFactor / (lineDistance * 0.25);
        } catch (Exception x) {
            warning("GA fitness error on {0}", x, this);
            return 0.0;
        }
    }

    private double isectF(Rectangle r, double cx, double cy, double rr, double x1, double y1, double x2, double y2) {
        if (r.intersectsLine(x1, y1, x2, y2)) {
            final double v = rr - Line2D.ptLineDist(x1, y1, x2, y2, cx, cy);
            return v >= 0.0 ? v : 1.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public void mutate(GaContext gc) {
        if (gc.random.nextFloat() < gc.getMutationProbability()) {
            final int r = gc.random.nextInt(2000);
            final int p = 2 * r + 1;
            for (int i = 0; i < COUNT; i++) {
                xs[i] += gc.random.nextInt(p) - r;
                ys[i] += gc.random.nextInt(p) - r;
            }
        }
    }

    @Override
    public LineSpecie crossover(GaContext gc, LineSpecie that) {
        final int[] xs = new int[COUNT], ys = new int[COUNT];
        for (int i = 0; i < COUNT; i++) {
            if (gc.random.nextBoolean()) {
                xs[i] = that.xs[i];
                ys[i] = that.ys[i];
            } else {
                xs[i] = this.xs[i];
                ys[i] = this.ys[i];
            }
        }
        return new LineSpecie(blockLink, xs, ys);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + Arrays.toString(xs) + Arrays.toString(ys);
    }
}
