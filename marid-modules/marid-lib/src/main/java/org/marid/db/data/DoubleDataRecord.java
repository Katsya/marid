/*
 * Copyright (C) 2013 Dmitry Ovchinnikov
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
package org.marid.db.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Adds a double data record.
 *
 * @author Dmitry Ovchinnikov (d.ovchinnikow at gmail.com)
 */
public class DoubleDataRecord extends NumericDataRecord<Double> {

    private double value;

    /**
     * Default constructor.
     */
    public DoubleDataRecord() {
        this("", 0L, 0.0);
    }

    /**
     * Constructs the double data record.
     * @param tag Tag.
     * @param ts Timestamp.
     * @param val Value.
     */
    public DoubleDataRecord(String tag, long ts, double val) {
        super(tag, ts);
        value = val;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public DoubleDataRecord clone() {
        return new DoubleDataRecord(getTag(), getTime(), value);
    }

    @Override
    public void setValue(Double val) {
        if (val == null) {
            throw new NullPointerException("Value is null");
        }
        value = val;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeDouble(value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        super.readExternal(in);
        value = in.readDouble();
    }
}