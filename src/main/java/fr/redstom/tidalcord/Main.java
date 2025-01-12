package fr.redstom.tidalcord;

import fr.redstom.tidalcord.data.TidalProcessInfo;
import fr.redstom.tidalcord.services.PersistenceService;
import fr.redstom.tidalcord.services.SettingsService;
import fr.redstom.tidalcord.services.TidalService;
import fr.redstom.tidalcord.ui.DialogManager;

import jakarta.annotation.PostConstruct;

import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.beans.BeanProperty;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class Main {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    private final DialogManager dialogManager;
    private final TidalService tidalService;
    private final SettingsService settings;

    @PostConstruct
    public void init() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            dialogManager.showError("This application is only supported on Windows.");
            System.exit(1);
        }
    }

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
