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
package org.marid.app.undertow;

import io.undertow.server.session.SslSessionConfig;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletSessionConfig;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.marid.app.web.MaridListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static javax.servlet.SessionTrackingMode.SSL;

@Component
public class MaridDeploymentInfo extends DeploymentInfo {

  public MaridDeploymentInfo() {
    setDeploymentName("marid");
    setClassLoader(Thread.currentThread().getContextClassLoader());
    setDefaultEncoding("UTF-8");
    setDisableCachingForSecuredPages(true);
    setInvalidateSessionOnLogout(true);
    setSecurityDisabled(true);
    setContextPath("/");
    setSessionConfigWrapper((sessionConfig, deployment) -> new SslSessionConfig(deployment.getSessionManager()));
    setDefaultSessionTimeout(3600);
    setCheckOtherSessionManagers(false);
    setServletSessionConfig(new ServletSessionConfig().setSessionTrackingModes(EnumSet.of(SSL)));
  }

  @Autowired
  public void setResourseManager(MaridResourceManager resourseManager) {
    setResourceManager(resourseManager);
  }

  @Autowired
  public void setListeners(MaridListener listener) {
    addListener(new ListenerInfo(MaridListener.class, new ImmediateInstanceFactory<>(listener), false));
  }
}
