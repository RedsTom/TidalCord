package fr.redstom.tidalcord.utils;

import de.jcm.discordgamesdk.LogLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;

@RequiredArgsConstructor
@Getter
public enum LogLevelAdapter {

    ERROR(Level.ERROR, LogLevel.ERROR),
    WARN(Level.WARN, LogLevel.WARN),
    INFO(Level.INFO, LogLevel.INFO),
    DEBUG(Level.DEBUG, LogLevel.DEBUG),
    TRACE(Level.TRACE, LogLevel.VERBOSE);

    private final Level slf4jLevel;
    private final LogLevel discordLevel;

    public static LogLevelAdapter fromDiscord(LogLevel level) {
        for(LogLevelAdapter adapter : values()) {
            if(adapter.discordLevel() == level) {
                return adapter;
            }
        }

        return DEBUG;
    }

    public static LogLevelAdapter fromSlf4jLevel(Level level) {
        for(LogLevelAdapter adapter : values()) {
            if(adapter.slf4jLevel() == level) {
                return adapter;
            }
        }
        return DEBUG;
    }
}
