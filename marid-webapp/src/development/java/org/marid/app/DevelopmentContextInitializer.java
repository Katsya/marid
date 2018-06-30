package org.marid.app;

import org.eclipse.rap.rwt.internal.RWTProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;

public class DevelopmentContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  @Override
  public void initialize(@NonNull GenericApplicationContext applicationContext) {
    System.setProperty(RWTProperties.DEVELOPMEMT_MODE, "true");

    applicationContext.getEnvironment().setActiveProfiles("development");
  }
}
