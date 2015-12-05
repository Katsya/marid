/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
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

package org.marid.ide.components;

import org.marid.logging.LogSupport;
import org.marid.pref.SysPrefSupport;
import org.marid.swing.StandardLookAndFeel;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * @author Dmitry Ovchinnikov.
 */
@Component
public class MaridLaf implements LogSupport, SysPrefSupport {

    public MaridLaf() {
        UIManager.installLookAndFeel("Standard", StandardLookAndFeel.class.getName());
        final String laf = SYSPREFS.get("laf", NimbusLookAndFeel.class.getName());
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception x) {
            Log.log(WARNING, "Unable to set LAF {0}", x, laf);
        }
        if (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel) {
            UIManager.put("Nimbus.keepAlternateRowColor", true);
        }
    }
}
