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

package org.marid.services.server;

import org.marid.services.*;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Dmitry Ovchinnikov
 */
public class ServiceServer extends AbstractService {

    private static final long serialVersionUID = -1475217680158305714L;
    private final Service delegate;

    public ServiceServer(Service delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void doStart() throws Exception {
    }

    @Override
    protected void doStop() throws Exception {
    }

    @Override
    public <T extends Response> Future<T> send(Request<T> message) {
        return delegate.send(message);
    }

    @Override
    public Transaction transaction(Map<String, Object> params) {
        return delegate.transaction(params);
    }
}
