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

import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONElement;
import kong.unirest.core.json.JSONObject;

public record TidalReturnInformation<T extends JSONElement>(T data, JSONArray included) {

    /**
     * Create a new TidalReturnInformation from a JSONObject and split it into data and included
     *
     * @param object The JSONObject to split
     * @return The TidalReturnInformation
     */
    public static TidalReturnInformation<JSONObject> from(JSONObject object) {
        return new TidalReturnInformation<>(
                object.getJSONObject("data"), object.getJSONArray("included"));
    }

    /**
     * Create a new TidalReturnInformation from a JSONObject and consider it as data
     *
     * @param object The JSONObject to consider as data
     * @return The TidalReturnInformation
     */
    public static <T extends JSONElement> TidalReturnInformation<T> fromData(T object) {
        return new TidalReturnInformation<>(object, new JSONArray());
    }
}
