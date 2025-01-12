package fr.redstom.tidalcord.services;

import fr.redstom.tidalcord.ui.DialogManager;
import fr.redstom.tidalcord.utils.Pair;
import fr.redstom.tidalcord.utils.Watcher;

import jakarta.annotation.PostConstruct;

import kong.unirest.core.HeaderNames;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.UnirestInstance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Getter
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class CredentialsService {

    private final DialogManager dialogManager;
    private final UnirestInstance unirest;
    private final SettingsService settingsService;

    private final Watcher<Pair<String, String>> tokens = new Watcher<>(new Pair<>("", ""));

    private final Watcher<Boolean> authenticated = new Watcher<>(false);
    private final Watcher<String> accessToken = new Watcher<>("");

    @PostConstruct
    public void init() {
        this.tokens.addListener(this::tryAuth, true);
    }

    public void updateCredentials(String clientId, String clientSecret) {
        this.tokens.set(new Pair<>(clientId, clientSecret));
    }

    public void tryAuth(Pair<String, String> creds) {
        tryAuth(creds, true);
    }

    private void tryAuth(Pair<String, String> creds, boolean showError) {
        String unencodedCreds = creds.first() + ":" + creds.second();
        String token = Base64.getEncoder().encodeToString(unencodedCreds.getBytes());

        unirest.post("https://auth.tidal.com/v1/oauth2/token")
                .header(HeaderNames.AUTHORIZATION, "Basic " + token)
                .field("grant_type", "client_credentials")
                .asJsonAsync(res -> this.completeAuth(res, showError));
    }

    private void completeAuth(HttpResponse<JsonNode> response, boolean showError) {
        if (response.isSuccess()) {
            this.accessToken.set(
                    "Bearer " + response.getBody().getObject().getString("access_token"));
            this.authenticated.set(true);
        } else {
            if (showError && !settingsService.firstError().get()) {
                dialogManager.showError("Invalid credentials.");
            }

            settingsService.firstError().set(false);
            dialogManager.showLoginDialog();
        }
    }
}
