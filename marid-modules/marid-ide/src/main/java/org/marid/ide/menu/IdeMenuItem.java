/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.ide.menu;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.weathericons.WeatherIcon;

import javax.enterprise.context.Dependent;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Dmitry Ovchinnikov
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Dependent
public @interface IdeMenuItem {

    @Nonbinding String menu() default "";

    @Nonbinding String text() default "";

    @Nonbinding FontAwesomeIcon[] faIcons() default {};

    @Nonbinding MaterialIcon[] mIcons() default {};

    @Nonbinding MaterialDesignIcon[] mdIcons() default {};

    @Nonbinding WeatherIcon[] wIcons() default {};

    @Nonbinding OctIcon[] oIcons() default {};

    @Nonbinding String group() default "";

    @Nonbinding String key() default "";

    @Nonbinding MenuItemType type() default MenuItemType.NORMAL;
}