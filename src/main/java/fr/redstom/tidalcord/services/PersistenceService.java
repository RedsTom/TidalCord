package fr.redstom.tidalcord.services;

import fr.redstom.tidalcord.ui.DialogManager;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class PersistenceService {

    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";

    private final Path configurationFilePath = Path.of("config.properties");
    private final Properties properties = new Properties();

    private final CredentialsService credentialsService;
    private final DialogManager dialogManager;

    /**
     * Initializes the configuration file and loads its content.
     *
     * @throws IOException If the configuration file cannot be created.
     */
    @PostConstruct
    public void init() throws IOException {
        if (!Files.exists(configurationFilePath)) {
            initialize();
        } else {
            properties.load(configurationFilePath.toUri().toURL().openStream());
        }

        credentialsService.updateCredentials(
                properties.getProperty(CLIENT_ID), properties.getProperty(CLIENT_SECRET));

        credentialsService.clientTokens().addListener(pair -> this.saveCredentials(pair.left(), pair.right()));
    }

    private void initialize() throws IOException {
        Files.createFile(configurationFilePath);
        saveCredentials("", "");
    }

    /**
     * Save the credentials to the configuration file.
     *
     * @param credentials The credentials to save.
     */
    private void saveCredentials(String clientId, String clientSecret) {
        properties.setProperty(CLIENT_ID, clientId);
        properties.setProperty(CLIENT_SECRET, clientSecret);

        this.save();
    }

    /**
     * Save the properties to the configuration file.
     */
    public void save() {
        try {
            BufferedWriter writer = Files.newBufferedWriter(configurationFilePath);
            properties.store(writer, "Update TidalCord configuration");
        } catch (IOException e) {
            dialogManager.showError("Failed to save configuration.");
        }
    }
}
