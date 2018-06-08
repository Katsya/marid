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

import com.vaadin.flow.data.provider.ListDataProvider;
import org.marid.applib.repository.Artifact;
import org.marid.ui.webide.base.dao.ArtifactDao;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.TreeSet;

@Component
public class ArtifactManager {

  private final ArtifactDao dao;
  private final TreeSet<Artifact> artifacts;
  private final ListDataProvider<Artifact> dataProvider;

  public ArtifactManager(ArtifactDao dao) {
    this.dao = dao;
    this.artifacts = new TreeSet<>(dao.loadArtifacts());
    this.dataProvider = new ListDataProvider<>(artifacts);
  }

  public ListDataProvider<Artifact> getDataProvider() {
    return dataProvider;
  }

  public void addArtifacts(Collection<Artifact> artifacts) {
    if (this.artifacts.addAll(artifacts)) {
      dataProvider.refreshAll();
    }
  }

  public void save() {
    dao.save(artifacts);
  }
}
