package fr.redstom.tidalcord.data;

import lombok.ToString;

import java.util.Arrays;

public record TidalProcessInfo(
        TidalState state,
        String name,
        String[] artists
) {

    public String info() {
        return name + " by " + String.join(", ", artists);
    }

}
