/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.marid.ui.webide.base.views.artifacts;

import com.vaadin.ui.Grid;
import org.marid.applib.repository.Artifact;
import org.marid.applib.spring.init.Init;
import org.marid.spring.annotation.SpringComponent;

@SpringComponent
public class ArtifactTable extends Grid<Artifact> {

  public ArtifactTable(ArtifactManager manager) {
    super(manager.getDataProvider());
    setSizeFull();
  }

  @Init
  public void initGroupId() {
    addColumn(Artifact::getGroupId)
        .setId("groupId")
        .setCaption("groupId")
        .setExpandRatio(2);
  }

  @Init
  public void initArtifactId() {
    addColumn(Artifact::getArtifactId)
        .setId("artifactId")
        .setCaption("artifactId")
        .setExpandRatio(3);
  }

  @Init
  public void initVersion() {
    addColumn(Artifact::getVersion)
        .setId("version")
        .setCaption("version")
        .setExpandRatio(1);
  }
}