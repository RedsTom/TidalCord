package fr.redstom.tidalcord.services;

import fr.redstom.tidalcord.ui.DialogManager;
import fr.redstom.tidalcord.utils.Pair;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

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

    @PostConstruct
    public void init() throws Exception {
        if (!Files.exists(configurationFilePath)) {
            Files.createFile(configurationFilePath);

            saveCredentials(new Pair<>("", ""));
        } else {
            properties.load(configurationFilePath.toUri().toURL().openStream());
        }

        credentialsService.updateCredentials(
                properties.getProperty(CLIENT_ID), properties.getProperty(CLIENT_SECRET));

        credentialsService.tokens().addListener(this::saveCredentials);
    }

    private void saveCredentials(Pair<String, String> stringStringPair) {
        properties.setProperty(CLIENT_ID, stringStringPair.first());
        properties.setProperty(CLIENT_SECRET, stringStringPair.second());

        this.save();
    }

    public void save() {
        try {
            properties.store(
                    Files.newBufferedWriter(configurationFilePath), "TidalCord configuration");
        } catch (IOException e) {
            dialogManager.showError("Failed to save configuration.");
        }
    }
}
