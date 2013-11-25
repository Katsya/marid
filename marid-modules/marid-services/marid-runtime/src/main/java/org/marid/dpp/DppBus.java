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

package org.marid.dpp;

import org.marid.groovy.ClosureChain;
import org.marid.methods.PropMethods;
import org.marid.tree.StaticTreeObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.marid.methods.PropMethods.getRejectedExecutionHandler;
import static org.marid.methods.PropMethods.getThreadFactory;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * @author Dmitry Ovchinnikov
 */
public class DppBus extends StaticTreeObject implements Runnable {

    protected final ThreadGroup timerThreadGroup;
    protected final ScheduledThreadPoolExecutor timer;
    protected final Map<String, ScheduledFuture<?>> taskMap = new HashMap<>();
    protected final boolean logDurations;
    protected final ClosureChain func;
    protected final ScheduledFuture<?> checkFuture;
    protected final boolean interruptThread;

    public DppBus(DppScheduler parent, String name, Map params) {
        super(parent, name, params);
        func = new ClosureChain(DppUtil.func(funcs(PropMethods.get(params, Object.class, "func"))));
        logDurations = PropMethods.get(params, boolean.class, "logDurations", parent.logDurations);
        timerThreadGroup = new ThreadGroup(label);
        timer = new ScheduledThreadPoolExecutor(
                PropMethods.get(params, int.class, "timerThreadCount", 1),
                getThreadFactory(params, "timerThreadFactory", timerThreadGroup,
                        PropMethods.get(params, boolean.class, "timerDaemon", false),
                        PropMethods.get(params, int.class, "timerStackSize", 0)),
                getRejectedExecutionHandler(params, "timerRejectedExecutionHandler"));
        timer.setRemoveOnCancelPolicy(
                PropMethods.get(params, boolean.class, "timerRemoveOnCancel", true));
        DppUtil.addTasks(logger, this, children, params);
        final long checkPeriod = PropMethods.get(params, long.class, "period", -1L);
        final TimeUnit checkTimeUnit = PropMethods.get(params, TimeUnit.class, "timeUnit", SECONDS);
        checkFuture = func.isEmpty() ? null : checkPeriod < 0L
                ? timer.schedule(this, 0L, checkTimeUnit)
                : timer.scheduleWithFixedDelay(this, 0L, checkPeriod, checkTimeUnit);
        interruptThread = PropMethods.get(params, boolean.class, "interruptThread", true);
    }

    private Iterable funcs(Object f) {
        return f == null ? Collections.emptyList() : Collections.singletonList(f);
    }

    @Override
    public DppScheduler parent() {
        return (DppScheduler) super.parent();
    }

    public void start() {
        if (!timer.isShutdown()) {
            for (final StaticTreeObject child : children.values()) {
                if (child instanceof DppTask) {
                    ((DppTask) child).start();
                }
            }
        }
    }

    public void stop() {
        if (!timer.isShutdown()) {
            if (checkFuture != null) {
                checkFuture.cancel(interruptThread);
            }
            for (final StaticTreeObject child : children.values()) {
                if (child instanceof DppTask) {
                    ((DppTask) child).stop();
                }
            }
            timer.shutdown();
            children.clear();
        }
    }

    @Override
    public void run() {
        func.call(logger, this, new HashMap<>());
    }
}