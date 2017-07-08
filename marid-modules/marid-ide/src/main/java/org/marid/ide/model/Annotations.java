/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.marid.ide.model;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.github.javaparser.JavaParser.parseName;
import static org.springframework.core.annotation.AnnotationAttributes.fromMap;

/**
 * @author Dmitry Ovchinnikov
 */
public interface Annotations {

    static Map<String, Expression> getMembers(AnnotationExpr expr) {
        if (expr instanceof MarkerAnnotationExpr) {
            return Collections.emptyMap();
        } else if (expr instanceof SingleMemberAnnotationExpr) {
            return Collections.singletonMap("value", ((SingleMemberAnnotationExpr) expr).getMemberValue());
        } else {
            final NormalAnnotationExpr annotationExpr = (NormalAnnotationExpr) expr;
            final ImmutableMap.Builder<String, Expression> builder = ImmutableMap.builder();
            annotationExpr.getPairs().forEach(p -> builder.put(p.getNameAsString(), p.getValue()));
            return builder.build();
        }
    }

    static boolean isLazy(NodeWithAnnotations<?> node) {
        return node.getAnnotationByClass(Lazy.class)
                .map(a -> {
                    final Map<String, Expression> map = getMembers(a);
                    if (map.isEmpty()) {
                        return true;
                    } else {
                        final Expression e = map.get("value");
                        return e instanceof BooleanLiteralExpr && ((BooleanLiteralExpr) e).getValue();
                    }
                })
                .orElse(false);
    }

    static boolean isPrototype(NodeWithAnnotations<?> node) {
        return node.getAnnotationByClass(Scope.class)
                .map(a -> {
                    final Map<String, Expression> map = getMembers(a);
                    if (map.isEmpty()) {
                        return false;
                    } else {
                        final Expression e = map.get("value");
                        return e instanceof StringLiteralExpr && "prototype".equals(((StringLiteralExpr) e).getValue());
                    }
                })
                .orElse(false);
    }

    static String value(NodeWithAnnotations<?> node) {
        return node.getAnnotationByClass(Value.class)
                .flatMap(a -> {
                    final Map<String, Expression> map = getMembers(a);
                    if (map.isEmpty()) {
                        return Optional.empty();
                    } else {
                        final Expression e = map.get("value");
                        return e instanceof StringLiteralExpr
                                ? Optional.ofNullable(((StringLiteralExpr) e).getValue())
                                : Optional.empty();
                    }
                })
                .orElse(null);
    }

    static String qualifier(NodeWithAnnotations<?> node) {
        return node.getAnnotationByClass(Qualifier.class)
                .flatMap(a -> {
                    final Map<String, Expression> map = getMembers(a);
                    if (map.isEmpty()) {
                        return Optional.empty();
                    } else {
                        final Expression e = map.get("value");
                        return e instanceof StringLiteralExpr
                                ? Optional.ofNullable(((StringLiteralExpr) e).getValue())
                                : Optional.empty();
                    }
                })
                .orElse(null);
    }

    static AnnotationExpr bean() {
        return new MarkerAnnotationExpr(parseName(Bean.class.getName()));
    }

    static AnnotationExpr generated(String generator) {
        return new SingleMemberAnnotationExpr(parseName(Generated.class.getName()), new StringLiteralExpr(generator));
    }

    static AnnotationExpr component() {
        return new MarkerAnnotationExpr(parseName(Component.class.getName()));
    }

    static AnnotationExpr lazy() {
        return new MarkerAnnotationExpr(parseName(Lazy.class.getName()));
    }

    static AnnotationExpr prototype() {
        return new SingleMemberAnnotationExpr(parseName(Scope.class.getName()), new StringLiteralExpr("prototype"));
    }

    static String beanName(Class<?> c) {
        final AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(c);
        final AnnotationBeanNameGenerator nameGenerator = new AnnotationBeanNameGenerator();
        return nameGenerator.generateBeanName(definition, new DefaultListableBeanFactory());
    }

    static String[] beanName(Method method) {
        final StandardMethodMetadata methodMetadata = new StandardMethodMetadata(method, true);
        final AnnotationAttributes bean = fromMap(methodMetadata.getAnnotationAttributes(Bean.class.getName(), false));
        if (bean == null) {
            return null;
        } else {
            final String[] names = bean.getStringArray("name");
            return names.length == 0 ? new String[] {method.getName()} : names;
        }
    }
}