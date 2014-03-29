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

package org.marid.swing;

import org.marid.image.MaridIcons;
import org.marid.pref.PrefSupport;
import org.marid.swing.util.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.marid.l10n.L10n.m;
import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov
 */
public class AbstractFrame extends JFrame implements PrefSupport {

    public AbstractFrame(String title) {
        super(s(title));
        setIconImages(MaridIcons.ICONS);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setState(getPref("state", getState()));
        setExtendedState(getPref("extendedState", getExtendedState()));
    }

    @Override
    public void pack() {
        super.pack();
        setBounds(getPref("bounds", new Rectangle(0, 0, 700, 500)));
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        switch (e.getID()) {
            case WindowEvent.WINDOW_OPENED:
                setState(getPref("state", getState()));
                setExtendedState(getPref("extendedState", getExtendedState()));
                break;
            case WindowEvent.WINDOW_CLOSED:
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                    putPref("bounds", getBounds());
                }
                putPref("state", getState());
                putPref("extendedState", getExtendedState());
                break;
        }
    }

    protected void showMessage(MessageType messageType, String title, Object message) {
        JOptionPane.showMessageDialog(this, message, s(title), messageType.messageType);
    }

    protected void showMessage(MessageType messageType, String title, String message, Object... args) {
        JOptionPane.showMessageDialog(this, m(message, args), s(title), messageType.messageType);
    }

    protected void showMessage(MessageType messageType, String title, String message, Throwable error, Object... args) {
        final StringWriter sw = new StringWriter();
        try (final PrintWriter pw = new PrintWriter(sw)) {
            pw.println(m(message, args));
            error.printStackTrace(pw);
        }
        JOptionPane.showMessageDialog(this, sw.toString(), s(title), messageType.messageType);
    }
}
