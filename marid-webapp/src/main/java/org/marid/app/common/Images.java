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
package org.marid.app.common;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.marid.applib.image.AppImage;
import org.marid.image.MaridIcon;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class Images {

  private final Path imageDirectory;
  private final String fallbackImage;
  private final Logger logger;
  private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

  public Images(Directories directories, Logger logger) throws IOException {
    this.imageDirectory = directories.getTempDir().resolve("images");
    this.fallbackImage = directories.getRwtDir()
        .resolve("rwt-resources")
        .resolve("resource")
        .resolve("static")
        .resolve("image")
        .resolve("blank.gif")
        .toString();
    this.logger = logger;

    Files.createDirectory(imageDirectory);
  }

  private String imageFile(AppImage image, int size) {
    return fileMap.computeIfAbsent(image.getImageUrl(size), url -> {
      final int lastDotIndex = url.lastIndexOf('.');
      final var suffix = lastDotIndex >= 0 ? url.substring(lastDotIndex) : ".tmp";
      try (final var stream = new URL(url).openStream()) {
        final var file = Files.createTempFile(imageDirectory, "img", suffix);
        Files.copy(stream, file, REPLACE_EXISTING);
        return file.toString();
      } catch (IOException x) {
        logger.warn("Unable to fetch image from {}", url, x);
        return fallbackImage;
      }
    });
  }

  private String imageFile(int size, int r, int g, int b, int a) {
    final var buffer = ByteBuffer.allocate(20).putInt(0, size).putInt(4, r).putInt(8, g).putInt(12, b).putInt(16, a);
    return fileMap.computeIfAbsent(Base64.getEncoder().encodeToString(buffer.array()) + ".gif", url -> {
      final var img = MaridIcon.getImage(size, r, g, b, a);
      try {
        final var file = Files.createTempFile(imageDirectory, "img", ".gif");
        ImageIO.write(img, "gif", file.toFile());
        return file.toString();
      } catch (IOException x) {
        throw new UncheckedIOException(x);
      }
    });
  }

  public Image image(Display display, AppImage image, int size) {
    return new Image(display, imageFile(image, size));
  }

  public Image maridIcon(Display display, int size, int r, int g, int b, int a) {
    return new Image(display, imageFile(size, r, g, b, a));
  }

  public Image maridIcon(Display display, int size, Color color) {
    return maridIcon(display, size, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }
}
