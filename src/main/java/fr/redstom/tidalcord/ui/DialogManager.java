package fr.redstom.tidalcord.ui;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.swing.*;

@Service
@RequiredArgsConstructor
public class DialogManager {

    private static final JFrame frame = new JFrame();

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
}
