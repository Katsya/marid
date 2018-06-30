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
package org.marid.spring.init;

import org.marid.spring.annotation.OrderedAnnotatedMethodProvider;
import org.marid.spring.events.ContextStartedListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.stream.IntStream;

public class InitBeanPostProcessor implements BeanPostProcessor {

  private static final OrderedAnnotatedMethodProvider INIT_METHODS = new OrderedAnnotatedMethodProvider(Init.class);

  private final GenericApplicationContext context;

  public InitBeanPostProcessor(GenericApplicationContext context) {
    this.context = context;
  }

  @Override
  public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) throws BeansException {
    if (bean == null) {
      return null;
    }

    final var methods = INIT_METHODS.get(bean.getClass());
    if (methods == null) {
      return bean;
    }

    try {
      if (bean.getClass().isAnnotationPresent(InitAfterStart.class)) {
        context.addApplicationListener(new ContextStartedListener() {
          @Override
          public void onApplicationEvent(@NonNull ContextStartedEvent event) {
            context.getApplicationListeners().remove(this);
            try {
              invoke(bean, context.getDefaultListableBeanFactory(), methods);
            } catch (RuntimeException x) {
              throw x;
            } catch (Exception x) {
              throw new IllegalStateException(x);
            }
          }
        });
      } else {
        invoke(bean, context.getDefaultListableBeanFactory(), methods);
      }
    } catch (Exception x) {
      throw new BeanInitializationException("Unable to initialize " + beanName, x);
    }
    return bean;
  }

  private void invoke(Object bean, DefaultListableBeanFactory beanFactory, Method[] methods) throws Exception {
    for (final var method : methods) {
      final var args = IntStream.range(0, method.getParameterCount())
          .mapToObj(i -> new DependencyDescriptor(new MethodParameter(method, i), true, true))
          .map(dd -> beanFactory.resolveDependency(dd, null))
          .toArray();
      method.invoke(bean, args);
    }
  }
}
