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

package org.marid.ide.profile;

import groovy.grape.GrapeIvy;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.io.File;

/**
 * @author Dmitry Ovchinnikov
 */
public class GrapeEngineImpl extends GrapeIvy {

    private transient MetaClass metaClass = InvokerHelper.getMetaClass(GrapeIvy.class);

    protected final File directory;

    public GrapeEngineImpl(File directory) {
        this.directory = directory;
        if (directory.mkdirs()) {
            assert directory.isDirectory();
        }
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return getMetaClass().invokeMethod(this, name, args);
    }

    @Override
    public Object getProperty(String propertyName) {
        return getMetaClass().getProperty(this, propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        getMetaClass().setProperty(this, propertyName, newValue);
    }

    @Override
    public MetaClass getMetaClass() {
        return metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public File getGroovyRoot() {
        return directory;
    }

    @Override
    public File getGrapeDir() {
        return directory;
    }
}