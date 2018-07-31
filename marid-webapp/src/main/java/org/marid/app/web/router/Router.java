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
package org.marid.app.web.router;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class Router {

  static final InheritableThreadLocal<HttpServletRequest> REQUEST = new InheritableThreadLocal<>();
  static final InheritableThreadLocal<HttpServletResponse> RESPONSE = new InheritableThreadLocal<>();
  static final InheritableThreadLocal<HttpSession> SESSION = new InheritableThreadLocal<>();

  public void register(HttpServletRequest request, HttpServletResponse response) {
    SESSION.set(request.getSession());
    REQUEST.set(request);
    RESPONSE.set(response);
  }
}
