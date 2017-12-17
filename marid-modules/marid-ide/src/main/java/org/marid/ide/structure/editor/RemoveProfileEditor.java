/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
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

package org.marid.ide.structure.editor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.marid.ide.common.Directories;
import org.marid.ide.project.ProjectManager;
import org.marid.jfx.action.SpecialAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class RemoveProfileEditor extends AbstractFileEditor<Path> {

  private final ProjectManager projectManager;
  private final SpecialAction removeAction;

  @Autowired
  public RemoveProfileEditor(Directories directories, ProjectManager projectManager, SpecialAction removeAction) {
    super(p -> Files.isDirectory(p) && p.getParent().equals(directories.getProfiles()));
    this.projectManager = projectManager;
    this.removeAction = removeAction;
  }

  @NotNull
  @Override
  public String getName() {
    return "Remove profile";
  }

  @NotNull
  @Override
  public String getIcon() {
    return "D_FOLDER_REMOVE";
  }

  @NotNull
  @Override
  public SpecialAction getSpecialAction() {
    return removeAction;
  }

  @Nullable
  @Override
  protected Path editorContext(@NotNull Path path) {
    return path;
  }

  @Override
  protected void edit(@NotNull Path path, @NotNull Path context) {
    projectManager.getProfiles().stream()
        .filter(p -> p.getName().equals(context.getFileName().toString()))
        .findFirst()
        .ifPresent(projectManager::remove);
  }
}
