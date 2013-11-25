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
 * Doubles data record.
 *
 * @author Dmitry Ovchinnikov (d.ovchinnikow at gmail.com)
 */
public class DoublesDataRecord extends ObjectDataRecord<double[]> {
    /**
     * Default constructor.
     */
    public DoublesDataRecord() {
        this("", 0L, new double[0]);
    }

    /**
     * Constructs a data record.
     * @param tag Tag.
     * @param ts Timestamp.
     * @param val Value.
     */
    public DoublesDataRecord(String tag, long ts, double[] val) {
        super(tag, ts, val);
    }

    @Override
    public DoublesDataRecord clone() {
        return new DoublesDataRecord(getTag(), getTime(), getValue());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        double[] value = getValue();
        out.writeInt(value.length);
        for (int i = 0; i < value.length; i++) {
            out.writeDouble(value[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        super.readExternal(in);
        double[] value = new double[in.readInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = in.readDouble();
        }
        setValue(value);
    }
}