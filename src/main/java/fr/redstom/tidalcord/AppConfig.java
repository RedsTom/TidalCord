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

import fr.redstom.tidalcord.services.CredentialsService;

import kong.unirest.core.HeaderNames;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for the application. */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final CredentialsService credentialsService;

    /**
     * Creates a new Unirest instance with the default base URL set to the Tidal API.
     *
     * @return The Unirest instance.
     */
    @Bean
    UnirestInstance unirest() {
        UnirestInstance instance = Unirest.spawnInstance();
        instance.config().defaultBaseUrl("https://openapi.tidal.com/v2");

        credentialsService
                .accessToken()
                .addListener(
                        token -> {
                            instance.config().setDefaultHeader(HeaderNames.AUTHORIZATION, token);
                        });

        return instance;
    }
}
