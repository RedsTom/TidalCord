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

import fr.redstom.tidalcord.data.TidalProcessInfo;
import fr.redstom.tidalcord.utils.BooleanWatcher;
import fr.redstom.tidalcord.utils.Watcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class SettingsService {

    /** Whether the software checks for music and displays it */
    private final BooleanWatcher enabled = new BooleanWatcher(true);

    /** The current song playing */
    private final Watcher<String> nowPlayingTitle = new Watcher<>("");
    private final Watcher<TidalProcessInfo> nowPlayingInfo = new Watcher<>(new TidalProcessInfo(null, "", new String[0]));

    /** If the software has already shown an error for this run */
    private final BooleanWatcher firstError = new BooleanWatcher(true);
}
