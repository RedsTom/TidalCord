package fr.redstom.tidalcord.services;

import fr.redstom.tidalcord.utils.Watcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class SettingsService {

    /** Whether the software checks for music and displays it */
    private final Watcher<Boolean> enabled = new Watcher<>(true);

    /** The current song playing */
    private final Watcher<String> nowPlaying = new Watcher<>("");

    /** If the software has already shown an error for this run */
    private final Watcher<Boolean> firstError = new Watcher<>(true);
}
