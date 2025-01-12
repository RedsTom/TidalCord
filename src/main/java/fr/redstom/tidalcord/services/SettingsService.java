package fr.redstom.tidalcord.services;

import fr.redstom.tidalcord.utils.Watcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class SettingsService {

    private final Watcher<Boolean> enabled = new Watcher<>(true);
    private final Watcher<String> nowPlaying = new Watcher<>("");
    private final Watcher<Boolean> firstError = new Watcher<>(true);
}
