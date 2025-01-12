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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

/** A handler for showing the popup menu when the tray icon is clicked */
public class TrayHandler extends MouseAdapter {
    private final JFrame transparentWindow;
    private final JPopupMenu popup;

    public TrayHandler(JFrame window, JPopupMenu popup) {
        this.transparentWindow = window;
        this.popup = popup;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            transparentWindow.setVisible(true);
            popup.setLocation(
                    e.getX() - popup.getPreferredSize().width / 2,
                    e.getY() - popup.getPreferredSize().height - 10);
            popup.setInvoker(popup);
            popup.setVisible(true);
        }
    }
}
