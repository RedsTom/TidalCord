package fr.redstom.tidalcord.ui;

import fr.redstom.tidalcord.services.CredentialsService;
import fr.redstom.tidalcord.utils.ImageUtils;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.event.WindowAdapter;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent.EventType;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class DialogManager {

    private static final JFrame frame = new JFrame();

    private final CredentialsService credentials;

    @PostConstruct
    public void init()
            throws UnsupportedLookAndFeelException,
                    ClassNotFoundException,
                    InstantiationException,
                    IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showLoginDialog() {
        // quit button next to login

        JDialog dialog = new JDialog(frame, "Provide developer tokens", true);
        dialog.setSize(700, 400);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setType(Window.Type.NORMAL);
        dialog.setLocationRelativeTo(null);
        dialog.setIconImage(ImageUtils.image("/assets/logo.png", 32, 32));
        dialog.setResizable(false);

        LayoutManager layout = new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS);
        dialog.setLayout(layout);

        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JEditorPane explainations = getExplanations(dialog);

        JTextField clientIdField = new JTextField(credentials.clientTokens().get().left());
        JPasswordField clientSecretField =
                new JPasswordField(credentials.clientTokens().get().right());

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(
                e -> {
                    credentials.updateCredentials(
                            clientIdField.getText(), new String(clientSecretField.getPassword()));
                    dialog.dispose();
                });

        dialog.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        credentials.updateCredentials(
                                clientIdField.getText(),
                                new String(clientSecretField.getPassword()));
                        dialog.dispose();
                    }
                });

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

        dialog.add(explainations);

        dialog.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.05;
        form.add(new JLabel("Client ID:"), gbc);

        gbc.gridy = 1;
        form.add(new JLabel("Client Secret:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.95;
        form.add(clientIdField, gbc);

        gbc.gridy = 1;
        form.add(clientSecretField, gbc);

        dialog.add(form);

        dialog.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        buttons.add(Box.createHorizontalGlue());
        buttons.add(quitButton);

        buttons.add(Box.createRigidArea(new Dimension(10, 0)));
        buttons.add(loginButton);

        dialog.add(buttons);

        dialog.pack();
        dialog.setVisible(true);
    }

    private JEditorPane getExplanations(JDialog dialog) {
        String htmlContent =
                """
<p style="font-family: Arial, sans-serif; user-select: none;">
    To work, TidalCord needs to connect to the Tidal API.<br>
    You need to create a Tidal API application to get the Client ID and Client Secret.<br>
    You can visit <a href="https://developer.tidal.com/dashboard/create">
    https://developer.tidal.com/dashboard/create</a> and create an application to get them.
</p>
""";

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(htmlContent);
        editorPane.setEditable(false);
        editorPane.setFont(dialog.getFont());
        editorPane.setBackground(dialog.getBackground());
        editorPane.setHighlighter(null);

        editorPane.setSize(
                new Dimension(
                        editorPane.getPreferredSize().width, 40)); // Ajuste la taille pour le texte
        editorPane.setFocusable(false);

        editorPane.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        editorPane.setCaretPosition(editorPane.getDocument().getLength());
                    }
                });

        editorPane.addHyperlinkListener(
                e -> {
                    if (e.getEventType() == EventType.ACTIVATED) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ex) {
                            showError("An error occurred while opening the link.");
                        }
                    }
                });
        return editorPane;
    }
}
