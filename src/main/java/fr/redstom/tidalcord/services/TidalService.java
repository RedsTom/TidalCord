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

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

import fr.redstom.tidalcord.data.TidalProcessInfo;
import fr.redstom.tidalcord.data.TidalState;
import fr.redstom.tidalcord.utils.Watcher;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TidalService {

    public static final String TIDAL_PROCESS_NAME = "TIDAL.exe";
    public static final Pattern TIDAL_PROCESS_PATTERN = Pattern.compile("(.+) - (?!\\{)(.+)");

    /**
     * Lists all the Tidal processes running on the system.
     *
     * @return The set of Tidal process ids
     */
    public Set<Long> tidalProcesses() {
        return ProcessHandle.allProcesses()
                .filter(ph -> ph.info().command().isPresent())
                .filter(ph -> ph.info().command().orElse("").endsWith(TIDAL_PROCESS_NAME))
                .map(ProcessHandle::pid)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the Tidal process info of the currently playing song. If no Tidal process is found,
     * the state is CLOSED. If a Tidal process is found but no song is playing, the state is OPENED.
     * If a Tidal process is found and a song is playing, the state is PLAYING. If an error occurs,
     * the state is ERROR.
     *
     * @return The Tidal process info
     */
    public TidalProcessInfo processInfo() {
        Set<Long> processes = tidalProcesses();
        Watcher<TidalProcessInfo> watcher =
                new Watcher<>(new TidalProcessInfo(TidalState.CLOSED, null, new String[0]));

        if (processes.isEmpty()) {
            return watcher.get();
        }

        User32.INSTANCE.EnumWindows(
                (hwnd, _) -> {
                    Optional<TidalProcessInfo> info = processInfo(hwnd, processes);

                    if (info.isEmpty()) {
                        return true;
                    }

                    if (info.get().state() != TidalState.PLAYING) {
                        watcher.setIf(
                                info.get(),
                                (a, b) -> a.state().importance() > b.state().importance());
                        return true;
                    }

                    watcher.set(info.get());
                    return false;
                },
                Pointer.NULL);

        return watcher.get();
    }

    /**
     * Takes a window handle and the set of Tidal process ids and returns the Tidal process info
     * with the song and artist names if found.
     *
     * @param hwnd The window handle
     * @param pids The set of Tidal process ids
     * @return The Tidal process info
     */
    private Optional<TidalProcessInfo> processInfo(WinDef.HWND hwnd, Set<Long> pids) {
        char[] windowTitle = new char[512];
        User32.INSTANCE.GetWindowText(hwnd, windowTitle, windowTitle.length);
        String title = new String(windowTitle).trim();

        // Filter out some windows that are not Tidal related
        if (List.of("", "MSCTFIME UI", "Default IME", "MediaPlayer SMTC window").contains(title)) {
            return Optional.empty();
        }

        IntByReference pidRef = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef);
        long pid = pidRef.getValue();

        if (!pids.contains(pid)) {
            return Optional.empty();
        }

        Matcher titleMatcher = TIDAL_PROCESS_PATTERN.matcher(title);
        if (!titleMatcher.find()) {
            return Optional.of(new TidalProcessInfo(TidalState.OPENED, null, null));
        }

        return Optional.of(
                new TidalProcessInfo(
                        TidalState.PLAYING,
                        titleMatcher.group(1),
                        titleMatcher.group(2).split(", ")));
    }
}
