package org.marid.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class DevelopmentContext {

  @Bean(initMethod = "start")
  public static Thread quitter(GenericApplicationContext context) {
    final Thread thread = new Thread(null, () -> {
      try (final Scanner scanner = new Scanner(System.in)) {
        while (scanner.hasNextLine()) {
          switch (scanner.nextLine().trim()) {
            case "q":
            case "quit":
              context.close();
              System.exit(0);
              break;
          }
        }
      }
    }, "quitter", 64L * 1024L, false);
    thread.setDaemon(true);
    return thread;
  }
}
