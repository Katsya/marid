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
package org.marid.app.undertow;

import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.session.SslSessionConfig;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ServletInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.DispatcherType;

@Component
public class DeploymentProvider {

  private final DeploymentInfo deploymentInfo = new DeploymentInfo()
      .setDeploymentName("marid")
      .setClassLoader(Thread.currentThread().getContextClassLoader())
      .setDefaultEncoding("UTF-8")
      .setDisableCachingForSecuredPages(true)
      .setInvalidateSessionOnLogout(true)
      .setSecurityDisabled(true)
      .setContextPath("/")
      .setSessionConfigWrapper((sessionConfig, deployment) -> new SslSessionConfig(deployment.getSessionManager()))
      .addWelcomePage("/app")
      .setDefaultSessionTimeout(3600);

  @Autowired
  public void setResourseManager() {
    final var classLoader = deploymentInfo.getClassLoader();
    deploymentInfo.setResourceManager(new ClassPathResourceManager(classLoader, "META-INF/resources/"));
  }

  @Autowired
  public void setServlets(ServletInfo[] servlets) {
    deploymentInfo.addServlets(servlets);
  }

  @Autowired
  public void setFilters(FilterInfo[] filters) {
    deploymentInfo.addFilters(filters);
    deploymentInfo.addFilterUrlMapping("securityFilter", "/app", DispatcherType.REQUEST);
  }

  public DeploymentInfo getDeploymentInfo() {
    return deploymentInfo;
  }
}
