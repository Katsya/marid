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

package org.marid.ide.menu;

import groovy.lang.Closure;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObjectSupport;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.marid.groovy.GroovyRuntime;
import org.marid.methods.PropMethods;
import org.marid.util.CollectionUtils;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Map.Entry;
import static java.util.Collections.emptyMap;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asType;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.plus;
import static org.marid.ide.menu.MenuType.ITEM;
import static org.marid.ide.menu.MenuType.MENU;
import static org.marid.methods.LogMethods.warning;

public class GroovyMenu extends GroovyObjectSupport {

    private static final Logger LOG = Logger.getLogger(GroovyMenu.class.getName());

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private static void loadEntries(URL script, List<MenuEntry> ens, ClassLoader l) throws Exception {
        final GroovyCodeSource gcs = new GroovyCodeSource(script);
        final Map<String, Object> map = (Map<String, Object>) GroovyRuntime.SHELL.evaluate(gcs);
        for (final Entry<String, Object> e : map.entrySet()) {
            fillEntries(null, e, ens);
        }
    }

    public static List<MenuEntry> getMenuEntries() {
        final LinkedList<MenuEntry> entries = new LinkedList<>();
        final ClassLoader l = Thread.currentThread().getContextClassLoader();
        try {
            final Enumeration<URL> e = l.getResources("Menu.groovy");
            while (e.hasMoreElements()) {
                final URL url = e.nextElement();
                try {
                    loadEntries(url, entries, l);
                } catch (Exception x) {
                    warning(LOG, "Unable to load {0}", x, url);
                }
            }
        } catch (Exception x) {
            warning(LOG, "Unable to load menu items", x);
        }
        return entries;
    }

    @SuppressWarnings("unchecked")
    static void fillEntries(final MenuEntry pr, final Entry<String, Object> e, final List<MenuEntry> ens) {
        final String name = e.getKey();
        final Map<String, Object> map = e.getValue() == null ?
                Collections.<String, Object>emptyMap() :
                (Map<String, Object>) e.getValue();
        final Object path = map.get("path");
        final String[] p;
        if (path instanceof Object[] || path instanceof Iterable) {
            p = asType(path, String[].class);
        } else if (pr == null) {
            p = new String[0];
        } else {
            p = CollectionUtils.concat(pr.getPath(), pr.getName());
        }
        final MenuEntry me = new MenuEntry() {
            @Override
            public String[] getPath() {
                return p;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getGroup() {
                return String.valueOf(map.get("group"));
            }

            @Override
            public String getLabel() {
                return isMutableLabel() ?
                        String.valueOf(map.get("label")) :
                        String.valueOf(DefaultGroovyMethods.get(map, "label",
                                StringGroovyMethods.capitalize(name).replace('_', ' ')));
            }

            @Override
            public String getCommand() {
                return PropMethods.get(map, String.class, "command", name);
            }

            @Override
            public String getShortcut() {
                return String.valueOf(map.get("shortcut"));
            }

            @Override
            public String getDescription() {
                final Object d = map.get("description");
                return isMutableDescription() ? ((Closure<String>) d).call() : String.valueOf(d);
            }

            @Override
            public String getInfo() {
                final Object i = map.get("info");
                return isMutableInfo() ? ((Closure<String>) i).call() : String.valueOf(i);
            }

            @Override
            public String getIcon() {
                final Object i = map.get("icon");
                return isMutableIcon() ? ((Closure<String>) i).call() : String.valueOf(i);
            }

            @Override
            public MenuType getType() {
                return !isLeaf() ? MENU : PropMethods.get(map, MenuType.class, "type", ITEM);
            }

            @Override
            public int getPriority() {
                return PropMethods.get(map, int.class, "priority", -1);
            }

            @Override
            public boolean isMutableIcon() {
                return map.get("icon") instanceof Closure;
            }

            @Override
            public boolean isMutableLabel() {
                return map.get("label") instanceof Closure;
            }

            @Override
            public boolean isMutableInfo() {
                return map.get("info") instanceof Closure;
            }

            @Override
            public boolean isMutableDescription() {
                return map.get("description") instanceof Closure;
            }

            @Override
            public boolean hasSelectedPredicate() {
                return map.get("selected") instanceof Closure;
            }

            @Override
            public boolean hasEnabledPredicate() {
                return map.get("enabled") instanceof Closure;
            }

            @Override
            public Boolean isSelected() {
                return hasSelectedPredicate() ? ((Closure<Boolean>) map.get("selected")).call() : null;
            }

            @Override
            public boolean isEnabled() {
                return hasEnabledPredicate() ? ((Closure<Boolean>) map.get("enabled")).call() : true;
            }

            @Override
            public boolean isLeaf() {
                for (final MenuEntry e : ens) {
                    if (e.getPath().length == getPath().length + 1 && Arrays.equals(e.getPath(), plus(getPath(), name))) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void call(ActionEvent event) {
                final Closure v = (Closure) map.get("action");
                if (v != null) {
                    try {
                        v.call(event);
                    } catch (Exception x) {
                        warning(LOG, "{0} calling error", x, this);
                    }
                }
            }

            @Override
            public String toString() {
                return DefaultGroovyMethods.join(CollectionUtils.concat(getPath(), getName()), "/");
            }
        };
        ens.add(me);
        final Map<String, Object> v = (Map<String, Object>) DefaultGroovyMethods.get(map, "items", emptyMap());
        if (v != null) {
            try {
                for (final Entry<String, Object> en : v.entrySet()) {
                    fillEntries(me, en, ens);
                }
            } catch (Exception x) {
                warning(LOG, "Adding elements error to {0}", x, me);
            }
        }
    }
}
