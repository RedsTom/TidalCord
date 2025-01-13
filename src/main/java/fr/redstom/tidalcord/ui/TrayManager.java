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

package fr.redstom.tidalcord.ui;

import fr.redstom.tidalcord.services.CredentialsService;
import fr.redstom.tidalcord.services.SettingsService;
import fr.redstom.tidalcord.ui.elements.CheckboxMenuItem;
import fr.redstom.tidalcord.ui.handlers.TrayCloseHandler;
import fr.redstom.tidalcord.ui.handlers.TrayHandler;
import fr.redstom.tidalcord.utils.ImageUtils;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.function.Consumer;

import javax.swing.*;

@Service
@RequiredArgsConstructor
public class TrayManager {

    private final DialogManager dialogManager;
    private final SettingsService settings;
    private final CredentialsService credentials;

    @Value("${app.version}")
    private final String appVersion = "1.0.0";

    private SystemTray tray;

    @PostConstruct
    public void init() {
        if (!SystemTray.isSupported()) {
            dialogManager.showError("System tray is not supported on this system.");
            System.exit(1);
        }

        this.tray = SystemTray.getSystemTray();

        try {
            initMenu();
        } catch (AWTException e) {
            dialogManager.showError("An error occurred while initializing the system tray.");
        }
    }

    private void initMenu() throws AWTException {
        Image logo = ImageUtils.image("/assets/logo.png", 16, 16);
        ImageIcon loginIcon = ImageUtils.icon("/assets/icons/key.png", 16, 16);
        ImageIcon notPlayingIcon = ImageUtils.icon("/assets/icons/pause.png", 16, 16);
        ImageIcon playingIcon = ImageUtils.icon("/assets/icons/playing.png", 16, 16);
        ImageIcon iconImage = ImageUtils.icon("/assets/icons/x.png", 16, 16);
        ImageIcon discordIcon = ImageUtils.icon("/assets/icons/discord.png");

        JPopupMenu popup = new JPopupMenu();
        popup.setMinimumSize(new Dimension(200, 100));

        JMenuItem titleItem = new JMenuItem("=== TidalCord v" + appVersion + " ===", new ImageIcon(logo));
        titleItem.setEnabled(false);
        titleItem.setMargin(new Insets(5, 0, 5, 0));
        titleItem.setFont(titleItem.getFont().deriveFont(Font.BOLD));

        JMenuItem nowPlayingItem = new JMenuItem("Loading...");
        nowPlayingItem.setEnabled(false);

        JMenuItem loginItem = new JMenuItem("", loginIcon);
        JMenuItem discordItem = new JMenuItem("", discordIcon);
        discordItem.setEnabled(false);

        CheckboxMenuItem enableItem = new CheckboxMenuItem("Enable");
        JMenuItem exitItem = new JMenuItem("Exit", iconImage);

        popup.add(titleItem);
        popup.add(nowPlayingItem);
        popup.addSeparator();
        popup.add(loginItem);
        popup.add(discordItem);
        popup.addSeparator();
        popup.add(enableItem);
        popup.addSeparator();
        popup.add(exitItem);

        loginItem.addActionListener(_ -> dialogManager.showLoginDialog());
        enableItem.addActionListener(_ -> settings.enabled().flip());
        exitItem.addActionListener(_ -> System.exit(0));
        settings.enabled().addListener(enableItem::setState);
        settings.nowPlayingTitle()
                .addListener(nowPlayingListener(nowPlayingItem, notPlayingIcon, playingIcon));
        settings.connectedUser().addListener(discordListener(discordItem));
        credentials.authenticated().addListener(authenticatedListener(loginItem, enableItem));

        TrayIcon icon = new TrayIcon(logo, "TidalCord");
        JFrame transparentWindow = createTrayWindow(popup);
        icon.addMouseListener(new TrayHandler(transparentWindow, popup));

        tray.add(icon);
    }

    private Consumer<String> discordListener(JMenuItem discordItem) {
        return (String text) ->
                discordItem.setText(
                        text.isEmpty() ? "Not connected to Discord" : "Connected to: " + text);
    }

    private static Consumer<Boolean> authenticatedListener(
            JMenuItem loginItem, CheckboxMenuItem enableItem) {
        return authenticated -> {
            if (authenticated) {
                loginItem.setText("Auth: Authenticated");
            } else {
                loginItem.setText("Auth: Not authenticated");
            }

            enableItem.setEnabled(authenticated);
        };
    }

    private static Consumer<String> nowPlayingListener(
            JMenuItem nowPlayingItem, ImageIcon notPlayingIcon, ImageIcon playingIcon) {
        return text -> {
            if (text.isEmpty()) {
                nowPlayingItem.setIcon(notPlayingIcon);
                nowPlayingItem.setText("Nothing playing...");
            } else {
                nowPlayingItem.setIcon(playingIcon);
                nowPlayingItem.setText("Now playing: " + text);
            }
        };
    }

    private JFrame createTrayWindow(JPopupMenu popup) {
        JFrame window = new JFrame();
        window.setType(JFrame.Type.UTILITY); // avoid task bar icon
        window.setUndecorated(true);
        window.setOpacity(0.0f);
        window.addFocusListener(new TrayCloseHandler(popup, window));

        return window;
    }
}
