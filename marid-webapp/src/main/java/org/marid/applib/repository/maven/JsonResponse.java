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
package org.marid.applib.repository.maven;

public class JsonResponse {

  public Response response;

  public static class Response {

    public int numFound;
    public int start;
    public Doc[] docs;
  }

  public static class Doc {

    public String id;
    public String g;
    public String a;
    public String latestVersion;
    public String p;
    public long timestamp;
    public int versionCount;
    public String[] ec;
  }
}
