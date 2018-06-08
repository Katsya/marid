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
package org.marid.app.common;

import com.vaadin.flow.component.UI;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.WeakHashMap;

@Component
public class UIContexts {

  private final WeakHashMap<UI, AnnotationConfigApplicationContext> contexts = new WeakHashMap<>();

  public void register(UI ui, AnnotationConfigApplicationContext context) {
    synchronized (contexts) {
      contexts.put(ui, context);
    }
  }

  public AnnotationConfigApplicationContext getContextFor(UI ui) {
    synchronized (contexts) {
      return contexts.get(ui);
    }
  }
}
