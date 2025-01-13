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

package fr.redstom.tidalcord;

import fr.redstom.tidalcord.data.TidalProcessInfo;
import fr.redstom.tidalcord.services.SettingsService;
import fr.redstom.tidalcord.services.TidalDetailsService;
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
    @Scheduled(cron = "0/1 * * * * *")
    public void checkTidal() {
        if (!settings.enabled().get()) {
            settings.nowPlayingTitle().set("");
            return;
        }

        TidalProcessInfo info = tidalService.processInfo();
        settings.nowPlayingTitle().set(info.info());
        settings.nowPlayingInfo().setCheck(info);
    }
}
