package fr.redstom.tidalcord.ui;

import fr.redstom.tidalcord.services.CredentialsService;
import fr.redstom.tidalcord.services.SettingsService;
import fr.redstom.tidalcord.ui.elements.CheckboxMenuItem;
import fr.redstom.tidalcord.utils.ImageUtils;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

@Service
@RequiredArgsConstructor
public class TrayManager {

    private final DialogManager dialogManager;
    private final SettingsService settings;
    private final CredentialsService credentials;
    private SystemTray tray;

    @PostConstruct
    public void init() {
        if (!SystemTray.isSupported()) {
            dialogManager.showError("System tray is not supported on this system.");
            System.exit(1);
        }

        this.tray = SystemTray.getSystemTray();

        initMenu();
    }

    private JFrame manageClose(JPopupMenu popup) {
        JFrame transparentWindow = new JFrame();
        transparentWindow.setType(JFrame.Type.UTILITY); // avoid task bar icon
        transparentWindow.setUndecorated(true);
        transparentWindow.setOpacity(0.0f);
        transparentWindow.addFocusListener(
                new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {}

                    @Override
                    public void focusLost(FocusEvent e) {
                        if (popup.isVisible()) popup.setVisible(false);
                        transparentWindow.dispose();
                    }
                });

        return transparentWindow;
    }

    private void initMenu() {
        Image logo = ImageUtils.image("/assets/logo.png", 16, 16);

        TrayIcon icon = new TrayIcon(logo, "TidalCord");

        JPopupMenu popup = new JPopupMenu();
        popup.setMinimumSize(new Dimension(200, 100));
        JFrame transparentWindow = manageClose(popup);

        JMenuItem titleItem = new JMenuItem("=== TidalCord ===", new ImageIcon(logo));
        titleItem.setEnabled(false);
        titleItem.setMargin(new Insets(5, 0, 5, 0));
        titleItem.setFont(titleItem.getFont().deriveFont(Font.BOLD));
        popup.add(titleItem);

        ImageIcon notPlayingIcon = ImageUtils.icon("/assets/icons/pause.png", 16, 16);
        ImageIcon playingIcon = ImageUtils.icon("/assets/icons/playing.png", 16, 16);

        JMenuItem nowPlayingItem = new JMenuItem("Loading...");
        nowPlayingItem.setEnabled(false);

        settings.nowPlaying()
                .addListener(
                        text -> {
                            if (text.isEmpty()) {
                                nowPlayingItem.setIcon(notPlayingIcon);
                                nowPlayingItem.setText("Nothing playing...");
                            } else {
                                nowPlayingItem.setIcon(playingIcon);
                                nowPlayingItem.setText("Now playing: " + text);
                            }
                        });

        popup.add(nowPlayingItem);

        popup.addSeparator();

        ImageIcon loginIcon = ImageUtils.icon("/assets/icons/key.png", 16, 16);
        JMenuItem loginItem = new JMenuItem("", loginIcon);

        loginItem.addActionListener(e -> {
            dialogManager.showLoginDialog();
        });
        popup.add(loginItem);

        CheckboxMenuItem enableItem = new CheckboxMenuItem("Enable");

        settings.enabled().addListener(enableItem::setState);
        enableItem.addActionListener(
                e -> {
                    settings.enabled().set(!settings.enabled().get());
                });
        credentials.authenticated().addListener(authenticated -> {
            loginItem.setText("Auth: " + (
                    authenticated ? "Authenticated" : "Not authenticated"
            ));

            enableItem.setEnabled(authenticated);
            enableItem.setToolTipText(authenticated ? "Enable TidalCord" : "You need to authenticate first");
        });

        popup.add(enableItem);

        popup.addSeparator();

        ImageIcon iconImage = ImageUtils.icon("/assets/icons/x.png", 16, 16);
        JMenuItem exitItem = new JMenuItem("Exit", iconImage);
        exitItem.addActionListener(_ -> System.exit(0));
        popup.add(exitItem);

        icon.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()
                                || (e.getButton() == MouseEvent.BUTTON1
                                        && e.getClickCount() == 2)) {
                            transparentWindow.setVisible(true);
                            popup.setLocation(
                                    e.getX() - popup.getPreferredSize().width / 2,
                                    e.getY() - popup.getPreferredSize().height - 10);
                            popup.setInvoker(popup);
                            popup.setVisible(true);
                        }
                    }
                });

        try {
            tray.add(icon);
        } catch (AWTException e) {
            dialogManager.showError("An error occurred while adding the tray icon.");
        }
    }
}
