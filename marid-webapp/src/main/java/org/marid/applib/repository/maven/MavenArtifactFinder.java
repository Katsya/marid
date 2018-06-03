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
package org.marid.applib.repository.maven;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.marid.applib.repository.Artifact;
import org.marid.applib.repository.ArtifactFinder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MavenArtifactFinder implements ArtifactFinder {

  private final URI url;
  private final ObjectMapper mapper = new ObjectMapper();

  MavenArtifactFinder(URI searchUrl) {
    url = searchUrl;
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  @Override
  public List<Artifact> find(String groupPattern, String artifactPattern, String classPattern) {
    final LinkedList<String> conditions = new LinkedList<>();

    if (groupPattern != null && !groupPattern.isEmpty()) {
      conditions.add("g:\"" + groupPattern + "\"");
    }

    if (artifactPattern != null && !artifactPattern.isEmpty()) {
      conditions.add("a:\"" + artifactPattern + "\"");
    }

    if (classPattern != null && !classPattern.isEmpty()) {
      conditions.add("c:\"" + classPattern + "\"");
    }

    final var query = "q=" + String.join("%32AND%32", conditions) + "&wt=json";

    try {
      final var uri = new URI(
          url.getScheme(), url.getRawUserInfo(), url.getHost(), url.getPort(), url.getRawPath(), query, null
      );
      final var connection = uri.toURL().openConnection();

      final JsonResponse response;
      try {
        connection.setAllowUserInteraction(false);
        connection.setConnectTimeout(10_000);
        connection.setUseCaches(false);
        connection.setReadTimeout(1_000);

        try (final var stream = connection.getInputStream()) {
          response = mapper.readValue(stream, JsonResponse.class);
        }
      } finally {
        if (connection != null) {
          if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).disconnect();
          }
        }
      }

      return Stream.of(response.response.docs).parallel()
          .flatMap(d -> Stream.of(d.ec)
              .map(StringUtils::stripFilenameExtension)
              .map(v -> v.startsWith("-") ? v.substring(1) : v)
              .distinct()
              .map(c -> new Artifact(d.g, d.a, d.latestVersion, c, d.p)))
          .sorted(Comparator
              .comparing(Artifact::getGroupId)
              .thenComparing(Artifact::getArtifactId)
              .thenComparing(Artifact::getVersion)
              .thenComparing(Artifact::getClassifier)
              .thenComparing(Artifact::getPackaging)
          )
          .collect(Collectors.toUnmodifiableList());
    } catch (IOException | URISyntaxException x) {
      throw new IllegalStateException(x);
    }
  }
}
