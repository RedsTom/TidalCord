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

package fr.redstom.tidalcord.services;

import fr.redstom.tidalcord.ui.DialogManager;
import fr.redstom.tidalcord.utils.Pair;
import fr.redstom.tidalcord.utils.Watcher;

import jakarta.annotation.PostConstruct;

import kong.unirest.core.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Base64;

/** Service handling the credentials and authentication on Tidal servers. */
@Service
@Getter
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class CredentialsService {

    private final DialogManager dialogManager;
    private final SettingsService settingsService;

    private final Watcher<Pair<String, String>> clientTokens = new Watcher<>(new Pair<>("", ""));

    private final Watcher<Boolean> authenticated = new Watcher<>(false);
    private final Watcher<String> accessToken = new Watcher<>("");

    /** Initialize the service. */
    @PostConstruct
    public void init() {
        this.clientTokens.addListener(tokens -> this.tryAuth(tokens.left(), tokens.right()), true);
    }

    /**
     * Update the credentials with the given values.
     *
     * @param clientId The client ID.
     * @param clientSecret The client secret.
     */
    public void updateCredentials(String clientId, String clientSecret) {
        this.clientTokens.set(new Pair<>(clientId, clientSecret));
    }

    /**
     * Try to authenticate with the given credentials.
     *
     * @param clientId The client ID.
     * @param clientSecret The client secret.
     */
    private void tryAuth(String clientId, String clientSecret) {
        String credentials = clientId + ":" + clientSecret;
        String token = Base64.getEncoder().encodeToString(credentials.getBytes());

        Unirest.post("https://auth.tidal.com/v1/oauth2/token")
                .header(HeaderNames.AUTHORIZATION, "Basic " + token)
                .field("grant_type", "client_credentials")
                .asJsonAsync(this::completeAuth);
    }

    /**
     * Complete the authentication process. If the response is successful, the access token is set
     * and the service is authenticated. If the response is not successful, an error is shown and
     * the login dialog is displayed.
     *
     * @param response The response from the authentication request.
     */
    private void completeAuth(HttpResponse<JsonNode> response) {
        if (!response.isSuccess()) {
            if (settingsService.firstError().get()) {
                settingsService.firstError().set(false);
            } else {
                dialogManager.showError("Invalid credentials.");
            }

            dialogManager.showLoginDialog();

            return;
        }

        this.accessToken.set("Bearer " + response.getBody().getObject().getString("access_token"));
        this.authenticated.set(true);

        if (settingsService.firstError().get()) {
            settingsService.firstError().set(false);
        } else {
            dialogManager.showSuccess("Successfully authenticated.");
        }
    }
}
