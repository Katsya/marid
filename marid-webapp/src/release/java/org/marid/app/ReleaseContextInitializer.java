package org.marid.app;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;

public class ReleaseContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  @Override
  public void initialize(@NonNull GenericApplicationContext context) {
    context.getEnvironment().setActiveProfiles("release");
  }
}
