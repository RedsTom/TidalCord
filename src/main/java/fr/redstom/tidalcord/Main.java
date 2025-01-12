package fr.redstom.tidalcord;

import fr.redstom.tidalcord.ui.DialogManager;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@RequiredArgsConstructor
public class Main {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    private final DialogManager dialogManager;
    @PostConstruct
    public void init() {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            dialogManager.showError("This application is only supported on Windows.");
            System.exit(1);
        }
    }
}
