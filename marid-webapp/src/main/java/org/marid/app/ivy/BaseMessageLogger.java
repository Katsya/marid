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

import org.apache.ivy.util.MessageLogger;

import java.util.Collections;
import java.util.List;

import static org.apache.ivy.util.Message.*;

@FunctionalInterface
public interface BaseMessageLogger extends MessageLogger {

  @Override
  default void rawlog(String msg, int level) {
    log(msg, level);
  }

  @Override
  default void debug(String msg) {
    log(msg, MSG_DEBUG);
  }

  @Override
  default void info(String msg) {
    log(msg, MSG_INFO);
  }

  @Override
  default void rawinfo(String msg) {
    info(msg);
  }

  @Override
  default void error(String msg) {
    log(msg, MSG_ERR);
  }

  @Override
  default void verbose(String msg) {
    log(msg, MSG_VERBOSE);
  }

  @Override
  default void warn(String msg) {
    log(msg, MSG_WARN);
  }

  @Override
  default void deprecated(String msg) {
    log(msg, MSG_WARN);
  }

  @Override
  default List<String> getProblems() {
    return Collections.emptyList();
  }

  @Override
  default List<String> getWarns() {
    return Collections.emptyList();
  }

  @Override
  default List<String> getErrors() {
    return Collections.emptyList();
  }

  @Override
  default void clearProblems() {
  }

  @Override
  default void sumupProblems() {
  }

  @Override
  default void endProgress() {
  }

  @Override
  default void endProgress(String msg) {
  }

  @Override
  default boolean isShowProgress() {
    return false;
  }

  @Override
  default void setShowProgress(boolean progress) {
  }

  @Override
  default void progress() {
  }
}
