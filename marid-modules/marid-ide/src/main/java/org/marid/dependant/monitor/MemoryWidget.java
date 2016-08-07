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

package org.marid.dependant.monitor;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.GridPane;
import org.marid.spring.annotation.Q;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
@Q(MonitorConfiguration.class)
@Order(1)
public class MemoryWidget extends AreaChart<Number, Number> {

    private final int count = 60;
    private final Runtime runtime = Runtime.getRuntime();

    public MemoryWidget() {
        super(new NumberAxis(), new NumberAxis());
        getData().addAll(Arrays.asList(
                freeMemory(),
                totalMemory(),
                usedMemory()
        ));
        setAnimated(false);
    }

    @Autowired
    private void init(GridPane monitorGridPane) {
        monitorGridPane.add(this, 0, 0);
    }

    private Series<Number, Number> freeMemory() {
        final Series<Number, Number> series = new Series<>();
        series.setName(s("Free memory, MiB"));
        series.getData().addAll(range(0, count).mapToObj(i -> new Data<Number, Number>(i, 0.0)).collect(toList()));
        return series;
    }

    private Series<Number, Number> totalMemory() {
        final Series<Number, Number> series = new Series<>();
        series.setName(s("Total memory, MiB"));
        series.getData().addAll(range(0, count).mapToObj(i -> new Data<Number, Number>(i, 0.0)).collect(toList()));
        return series;
    }

    private Series<Number, Number> usedMemory() {
        final Series<Number, Number> series = new Series<>();
        series.setName(s("Used memory, MiB"));
        series.getData().addAll(range(0, count).mapToObj(i -> new Data<Number, Number>(i, 0.0)).collect(toList()));
        return series;
    }

    @Override
    public NumberAxis getXAxis() {
        return (NumberAxis) super.getXAxis();
    }

    @Override
    public NumberAxis getYAxis() {
        return (NumberAxis) super.getYAxis();
    }

    @Scheduled(fixedRate = 250L)
    private void tick() {
        final long free = runtime.freeMemory(), total = runtime.totalMemory(), used = total - free;
        final double mib = 1024.0 * 1024.0;
        final Double[] values = {free / mib, total / mib, used / mib};
        Platform.runLater(() -> {
            for (int i = 0; i < getData().size(); i++) {
                final Series<Number, Number> series = getData().get(i);
                final ObservableList<Data<Number, Number>> data = series.getData();
                for (int j = 1; j < count; j++) {
                    data.get(j - 1).setYValue(data.get(j).getYValue());
                }
                data.get(count - 1).setYValue(values[i]);
            }
        });
    }
}
