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

package org.marid.ide.widgets.memory;

import org.marid.dyn.MetaInfo;
import org.marid.swing.ComponentConfiguration;
import org.marid.swing.forms.Tab;
import org.marid.swing.input.BooleanInputControl;
import org.marid.swing.input.SpinIntInputControl;
import org.springframework.stereotype.Component;

/**
 * @author Dmitry Ovchinnikov.
 */
@Component
@Tab(node = "chart")
public class MemoryWidgetConfiguration extends ComponentConfiguration {

    @MetaInfo(group = "chart")
    public final P<Boolean> useBuffer = p("useBuffer", BooleanInputControl::new, () -> true);

    @MetaInfo(group = "chart")
    public final P<Boolean> save = p("save", BooleanInputControl::new, () -> true);

    @MetaInfo(group = "chart")
    public final P<Boolean> print = p("print", BooleanInputControl::new, () -> true);

    @MetaInfo(group = "chart")
    public final P<Boolean> zoom = p("zoom", BooleanInputControl::new, () -> true);

    @MetaInfo(group = "tooltips")
    public final P<Boolean> tooltips = p("tooltips", BooleanInputControl::new, () -> true);

    @MetaInfo(group = "chart")
    public final P<Integer> updateInterval = p("updateInterval", () -> new SpinIntInputControl(1, 10, 1), () -> 1);

    @MetaInfo(group = "chart")
    public final P<Integer> historySize = p("historySize", () -> new SpinIntInputControl(1, 60, 1), () -> 3);
}
