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

package org.marid.proto.health;

import org.marid.proto.ProtoBus;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Dmitry Ovchinnikov
 */
public class StdProtoBusHealthMonitor implements AutoCloseable {

    private final ScheduledFuture<?> task;

    public StdProtoBusHealthMonitor(ProtoBus bus, ScheduledExecutorService scheduler, StdProtoBusHealthMonitorProps props) {
        final long timeout = props.getMaxRecencySeconds() * 1000L;
        final Runnable resetStrategy = () -> {
            final long timestamp = bus.getHealth().getLastSuccessfulTransactionTimestamp().getTime();
            final long now = System.currentTimeMillis();
            if (now - timestamp > timeout) {
                bus.getHealth().reset();
                bus.reset();
            }
        };
        task = scheduler.scheduleWithFixedDelay(resetStrategy, props.getDelaySeconds(), props.getPeriodSeconds(), SECONDS);
    }

    @Override
    public void close() throws Exception {
        if (task != null) {
            task.cancel(false);
        }
    }
}