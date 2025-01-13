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
import fr.redstom.tidalcord.data.TidalReturnInformation;
import fr.redstom.tidalcord.data.TidalTrackInformation;
import fr.redstom.tidalcord.utils.Watcher;

import jakarta.annotation.PostConstruct;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.UnirestInstance;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Getter
@RequiredArgsConstructor
public class TidalDetailsService {

    private final UnirestInstance unirest;
    private final SettingsService settingsService;

    private final Watcher<TidalTrackInformation> nowPlaying = new Watcher<>(null);

    @PostConstruct
    public void init() {
        settingsService.nowPlayingInfo().addListener(this::accept);

        settingsService
                .enabled()
                .addListener(
                        enabled -> {
                            if (enabled) {
                                return;
                            }

                            nowPlaying.set(null);
                        });
    }

    public TidalTrackInformation fromProcessInfo(TidalProcessInfo info) {
        HttpResponse<JsonNode> response =
                unirest.get("/searchresults/" + info.query())
                        .queryString("countryCode", "US")
                        .queryString("include", "tracks")
                        .queryString("limit", 1)
                        .asJson();

        if (!response.isSuccess()) {
            return null;
        }

        JSONObject data = response.getBody().getObject().getJSONObject("data");
        JSONObject relationships = data.getJSONObject("relationships");
        JSONObject tracks = relationships.getJSONObject("tracks");
        JSONObject jsonObject = tracks.getJSONArray("data").getJSONObject(0);

        String trackId = jsonObject.getString("id");

        TidalReturnInformation<JSONObject> track = track(trackId);
        if (track == null) {
            return null;
        }

        TidalReturnInformation<JSONObject> album = album(track);
        TidalReturnInformation<JSONArray> artists = artists(track);

        String title = track.data().getJSONObject("attributes").getString("title");

        String durationStr = track.data().getJSONObject("attributes").getString("duration");
        Duration duration = Duration.parse(durationStr);

        String coverUrl =
                album.data()
                        .getJSONObject("attributes")
                        .getJSONArray("imageLinks")
                        .getJSONObject(0)
                        .getString("href");

        String albumTitle = album.data().getJSONObject("attributes").getString("title");

        return new TidalTrackInformation(
                title, artistNames(artists), albumTitle, coverUrl, duration);
    }

    public TidalReturnInformation<JSONObject> track(String trackId) {
        HttpResponse<JsonNode> response =
                unirest.get("/tracks/{id}")
                        .routeParam("id", trackId)
                        .queryString("countryCode", "US")
                        .queryString("include", "albums, artists")
                        .asJson();

        if (!response.isSuccess()) {
            return null;
        }

        JsonNode object = response.getBody();
        return TidalReturnInformation.from(object.getObject());
    }

    public JSONArray includedOfType(String type, TidalReturnInformation<?> track) {
        JSONArray array = new JSONArray();

        JSONArray included = track.included();
        for (int i = 0; i < included.length(); i++) {
            JSONObject object = included.getJSONObject(i);
            if (object.getString("type").equals(type)) {
                array.put(object);
            }
        }

        return array;
    }

    public TidalReturnInformation<JSONObject> album(TidalReturnInformation<?> track) {
        return TidalReturnInformation.fromData(includedOfType("albums", track).getJSONObject(0));
    }

    public TidalReturnInformation<JSONArray> artists(TidalReturnInformation<?> track) {
        return TidalReturnInformation.fromData(includedOfType("artists", track));
    }

    private String[] artistNames(TidalReturnInformation<JSONArray> artists) {
        String[] names = new String[artists.data().length()];

        for (int i = 0; i < artists.data().length(); i++) {
            JSONObject artist = artists.data().getJSONObject(i);
            names[i] = artist.getJSONObject("attributes").getString("name");
        }

        return names;
    }

    private void accept(TidalProcessInfo info) {
        TidalTrackInformation tidalTrackInformation = this.fromProcessInfo(info);
        nowPlaying.setCheck(tidalTrackInformation);
    }
}
