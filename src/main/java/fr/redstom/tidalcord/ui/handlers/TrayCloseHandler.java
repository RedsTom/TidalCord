/*
 * Copyright © 2025 Tom BUTIN (RedsTom)
 *
 * This file is part of TidalCord.
 *
 * TidalCord is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * TidalCord is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * The full license text can be found in the file `/LICENSE.md` at the root of
 * this project.
 */

package fr.redstom.tidalcord.ui.handlers;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

/** A handler for closing the tray popup and the transparent window when the focus is lost */
public class TrayCloseHandler implements FocusListener {
    private final JPopupMenu popup;
    private final JFrame transparentWindow;

    public TrayCloseHandler(JPopupMenu popup, JFrame transparentWindow) {
        this.popup = popup;
        this.transparentWindow = transparentWindow;
    }

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {
        if (popup.isVisible()) popup.setVisible(false);
        transparentWindow.dispose();
    }
}
