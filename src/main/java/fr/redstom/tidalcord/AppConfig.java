package fr.redstom.tidalcord;

import fr.redstom.tidalcord.services.CredentialsService;
import kong.unirest.core.HeaderNames;
import kong.unirest.core.ObjectMapper;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final CredentialsService credentialsService;

    @Bean
    UnirestInstance unirest() {
        UnirestInstance instance = Unirest.spawnInstance();
        instance.config().defaultBaseUrl("https://api.tidal.com/v2/");

        credentialsService.accessToken().addListener(token -> {
            instance.config().setDefaultHeader(HeaderNames.AUTHORIZATION, token);
        });

        return instance;
    }
}
