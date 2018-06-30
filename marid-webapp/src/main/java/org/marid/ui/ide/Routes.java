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
package org.marid.ui.ide;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface Routes {

  static Set<Class<? extends Component>> allNavigationTargets() {
    final var classLoader = Thread.currentThread().getContextClassLoader();
    final var cpResolver = new ClassPathScanningCandidateComponentProvider(false);
    cpResolver.setResourceLoader(new PathMatchingResourcePatternResolver(classLoader));
    cpResolver.addIncludeFilter(new AnnotationTypeFilter(Route.class, false, false));
    final var beanDefinitions = cpResolver.findCandidateComponents(Routes.class.getPackageName());
    return beanDefinitions.stream()
        .map(BeanDefinition::getBeanClassName)
        .filter(Objects::nonNull)
        .map(c -> ClassUtils.resolveClassName(c, classLoader))
        .filter(Component.class::isAssignableFrom)
        .map(c -> (Class<? extends Component>) c.asSubclass(Component.class))
        .collect(Collectors.toUnmodifiableSet());
  }
}
