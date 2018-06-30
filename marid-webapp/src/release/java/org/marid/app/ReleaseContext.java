package org.marid.app;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class ReleaseContext {

  @Bean
  public Closeable pidFile(Logger logger) throws IOException {
    final var pidFile = new File("marid-webapp.pid");
    Files.write(pidFile.toPath(), List.of(Long.toString(ProcessHandle.current().pid())));
    pidFile.deleteOnExit();
    return () -> {
      if (!pidFile.delete()) {
        logger.warn("Unable to delete {}", pidFile);
      }
    };
  }
}
