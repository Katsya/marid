/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.applib.image;

import org.jetbrains.annotations.NotNull;

public enum IaIcon implements AppImage {

  // awicons
  ADD("awicons", "vista-artistic", "add-icon"),
  // visualpharm
  UPDATE("visualpharm", "must-have", "Refresh-icon"),
  // turbomilk
  EDIT("turbomilk", "livejournal-10", "pencil-icon"),
  // bokehlicia
  PREFERENCES("bokehlicia", "captiva", "preferences-icon"),
  // graphicloads
  FIND("graphicloads", "100-flat", "zoom-seach-icon"),
  BACK("graphicloads", "100-flat-2", "arrow-back-icon"),
  // rafigul-hassan
  CANCEL("rafiqul-hassan", "blogger", "Close-icon"),
  // artdesigner
  ARTIFACT("artdesigner", "my-secret", "diamond-icon"),
  // oxygen-icons.org
  REPOSITORY("oxygen-icons.org", "oxygen", "Places-repository-icon"),
  PROJECT("oxygen-icons.org", "oxygen", "Actions-project-development-new-template-icon"),
  REMOVE("oxygen-icons.org", "oxygen", "Actions-list-remove-icon"),
  REFRESH("oxygen-icons.org", "oxygen", "Actions-view-refresh-icon"),
  SAVE("oxygen-icons.org", "oxygen", "Actions-document-save-icon"),
  // papirus-team
  SELECTOR("papirus-team", "papirus-apps", "chromium-app-list-icon"),
  // fatcow
  GROUP("fatcow", "farm-fresh", "radiobutton-group-icon"),
  CLASS("fatcow", "farm-fresh", "ip-class-icon"),
  SELECT_ALL("fatcow", "farm-fresh", "layer-select-icon"),
  DESELECT_ALL("fatcow", "farm-fresh", "select-invert-icon");

  private final String author;
  private final String type;
  private final String name;

  IaIcon(String author, String type, String name) {
    this.author = author;
    this.type = type;
    this.name = name;
  }

  @NotNull
  @Override
  public String getImageUrl(int size) {
    return "http://icons.iconarchive.com/icons/" + author + "/" + type + "/" + size + "/" + name + ".png";
  }
}
