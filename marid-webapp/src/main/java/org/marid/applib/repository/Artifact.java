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
package org.marid.applib.repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.marid.applib.model.Elem;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Artifact implements Comparable<Artifact>, Elem<Artifact> {

  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String classifier;
  private final String packaging;

  @JsonCreator
  public Artifact(String groupId, String artifactId, String version, String classifier, String packaging) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.classifier = classifier == null ? "" : classifier;
    this.packaging = packaging == null ? "jar" : packaging;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getClassifier() {
    return classifier;
  }

  public String getPackaging() {
    return packaging;
  }

  @NotNull
  @Override
  @JsonIgnore
  public Artifact getId() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Artifact)) {
      return false;
    } else {
      final Artifact that = (Artifact) obj;
      return Arrays.equals(
          new Object[] {this.groupId, this.artifactId, this.version, this.classifier, this.packaging},
          new Object[] {that.groupId, that.artifactId, that.version, that.classifier, that.packaging}
      );
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, version, classifier, packaging);
  }

  @Override
  public int compareTo(@NotNull Artifact o) {
    int c = groupId.compareTo(o.groupId);
    if (c != 0) {
      return c;
    }

    c = artifactId.compareTo(o.artifactId);
    if (c != 0) {
      return c;
    }

    c = version.compareTo(o.version);
    if (c != 0) {
      return c;
    }

    c = classifier.compareTo(o.classifier);
    if (c != 0) {
      return c;
    }

    return packaging.compareTo(o.packaging);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + Map.of(
        "groupId", groupId,
        "artifactId", artifactId,
        "version", version,
        "classifier", classifier,
        "packaging", packaging
    );
  }
}
