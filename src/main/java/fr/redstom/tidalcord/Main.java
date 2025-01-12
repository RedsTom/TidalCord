package fr.redstom.tidalcord;

import fr.redstom.tidalcord.data.TidalProcessInfo;
import fr.redstom.tidalcord.services.SettingsService;
import fr.redstom.tidalcord.services.TidalService;
import fr.redstom.tidalcord.ui.DialogManager;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/** Main class of the application. Manages the application lifecycle. */
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class Main {
    /** Main method of the application. */
    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    private final DialogManager dialogManager;
    private final TidalService tidalService;
    private final SettingsService settings;

    /** Initialize the application. Check if the application is running on Windows. */
    @PostConstruct
    public void init() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            dialogManager.showError("This application is only supported on Windows.");
            System.exit(1);
        }
    }

    /** Check the current song playing every 5 seconds. Updates Discord presence accordingly. */
    @Scheduled(cron = "0/5 * * * * *")
    public void checkTidal() {
        if (!settings.enabled().get()) {
            settings.nowPlaying().set("");
            return;
        }

        TidalProcessInfo info = tidalService.processInfo();
        settings.nowPlaying().set(info.info());
    }
}
