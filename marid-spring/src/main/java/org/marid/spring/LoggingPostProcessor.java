/*-
 * #%L
 * marid-spring
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

package org.marid.spring;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

import static java.util.logging.Level.INFO;
import static org.marid.logging.Log.log;

public class LoggingPostProcessor implements DestructionAwareBeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    if (beanName != null) {
      log(INFO, "Initializing {0}", beanName);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    if (beanName != null) {
      log(INFO, "Initialized {0} {1}", beanName, bean);
    } else {
      log(INFO, "Initialized {0}", bean);
    }
    return bean;
  }

  @Override
  public void postProcessBeforeDestruction(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    log(INFO, "Destroying {0} {1}", beanName, bean);
  }
}
