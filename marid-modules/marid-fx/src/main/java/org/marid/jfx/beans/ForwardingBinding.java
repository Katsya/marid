/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
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

package org.marid.jfx.beans;

import javafx.beans.binding.Binding;
import javafx.collections.ObservableList;

/**
 * @author Dmitry Ovchinnikov.
 * @since 0.8
 */
public class ForwardingBinding<T, V extends Binding<T>> extends ForwardingObservableValue<T, V> implements Binding<T> {

    public ForwardingBinding(V delegate) {
        super(delegate);
    }

    @Override
    public boolean isValid() {
        return delegate.isValid();
    }

    @Override
    public void invalidate() {
        delegate.invalidate();
    }

    @Override
    public ObservableList<?> getDependencies() {
        return delegate.getDependencies();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }
}