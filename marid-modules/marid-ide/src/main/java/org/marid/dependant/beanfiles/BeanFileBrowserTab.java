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

package org.marid.dependant.beanfiles;

import org.marid.ide.project.ProjectProfile;
import org.marid.ide.tabs.IdeKeyTab;
import org.springframework.stereotype.Component;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanFileBrowserTab extends IdeKeyTab {

    private final ProjectProfile profile;

    public BeanFileBrowserTab(ProjectProfile profile, BeanFileBrowserPane pane) {
        super(pane, profile.getName());
        this.profile = profile;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BeanFileBrowserTab && ((BeanFileBrowserTab) obj).profile.equals(profile);
    }
}