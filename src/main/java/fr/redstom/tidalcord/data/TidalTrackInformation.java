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

package fr.redstom.tidalcord.data;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

public record TidalTrackInformation(
        String title, String[] artists, String album, String coverUrl, Duration duration) {

    @Override
    public String toString() {
        return "TidalTrackInformation{"
                + "title='"
                + title
                + '\''
                + ", artists="
                + Arrays.toString(artists)
                + ", album='"
                + album
                + '\''
                + ", coverUrl='"
                + coverUrl
                + '\''
                + ", duration="
                + duration
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TidalTrackInformation that = (TidalTrackInformation) o;
        return Objects.equals(title, that.title)
                && Objects.equals(album, that.album)
                && Objects.equals(coverUrl, that.coverUrl)
                && Objects.deepEquals(artists, that.artists)
                && Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, Arrays.hashCode(artists), album, coverUrl, duration);
    }
}
