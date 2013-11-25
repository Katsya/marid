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

package org.marid.tree;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.marid.methods.PropMethods;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Map.Entry;
import static org.marid.util.CollectionUtils.concat;

/**
 * @author Dmitry Ovchinnikov
 */
public class StaticTreeObject implements TreeObject {

    protected final StaticTreeObject parent;
    protected final ConcurrentMap<String, Object> vars = new ConcurrentHashMap<>();
    protected final String[] path;
    protected final Map<String, StaticTreeObject> children = new LinkedHashMap<>();
    protected final String label;
    protected final Logger logger;

    public StaticTreeObject(StaticTreeObject parent, String name, Map params) {
        this.parent = parent;
        this.path = parent == null ? new String[] {name} : concat(parent.path, name);
        for (final Object e : PropMethods.get(params, Map.class, "vars", emptyMap()).entrySet()) {
            vars.put(String.valueOf(((Entry) e).getKey()), ((Entry) e).getValue());
        }
        this.label = DefaultGroovyMethods.join(path, "/");
        this.logger = Logger.getLogger(label);
    }

    @Override
    public StaticTreeObject parent() {
        return parent;
    }

    @Override
    public Collection<? extends StaticTreeObject> children() {
        return unmodifiableCollection(children.values());
    }

    @Override
    public Object get(String name) {
        return vars.get(name);
    }

    @Override
    public <T> T get(Class<T> type, String key, T def) {
        final T value = get(type, key);
        return value == null ? def : value;
    }

    @Override
    public <T> T get(Class<T> type, String key) {
        return PropMethods.get(vars, type, key);
    }

    @Override
    public Object getAt(String name) {
        return get(name);
    }

    @Override
    public Object put(String name, Object value) {
        return vars.put(name, value);
    }

    @Override
    public void putAt(String name, Object value) {
        put(name, value);
    }

    @Override
    public Set<String> keySet() {
        return vars.keySet();
    }

    @Override
    public StaticTreeObject object(String... path) {
        return object(Arrays.asList(path));
    }

    @Override
    public StaticTreeObject object(List<String> path) {
        try {
            if (path.isEmpty()) {
                return this;
            } else if (".".equals(path.get(0))) {
                return object(path.subList(1, path.size()));
            } else if ("..".equals(path.get(0))) {
                return parent.object(path.subList(1, path.size()));
            } else {
                return children.get(path.get(0)).object(path.subList(1, path.size()));
            }
        } catch (NullPointerException x) {
            throw new IllegalArgumentException("Apply " + path + " to " + listPath(), x);
        }
    }

    @Override
    public String name() {
        return path[path.length - 1];
    }

    @Override
    public String path() {
        return label;
    }

    @Override
    public String[] arrayPath() {
        return path;
    }

    @Override
    public List<String> listPath() {
        return Arrays.asList(path);
    }

    @Override
    public ConcurrentMap<String, Object> vars() {
        return vars;
    }

    @Override
    public StaticTreeObject getRoot() {
        for (StaticTreeObject o = this; o != null; o = o.parent) {
            if (o.parent == null) {
                return o;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}