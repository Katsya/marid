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

package org.marid.app.ivy;

import org.slf4j.Logger;

import static org.apache.ivy.util.Message.*;

public class IvySlfLoggerAdapter implements BaseMessageLogger {

  private final Logger logger;

  public IvySlfLoggerAdapter(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void log(String msg, int level) {
    switch (level) {
      case MSG_INFO:
        logger.info(msg);
        break;
      case MSG_DEBUG:
        logger.debug(msg);
        break;
      case MSG_ERR:
        logger.error(msg);
        break;
      case MSG_WARN:
        logger.warn(msg);
        break;
      case MSG_VERBOSE:
        logger.trace(msg);
        break;
    }
  }
}
