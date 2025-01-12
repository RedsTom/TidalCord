package fr.redstom.tidalcord.data;

public record TidalProcessInfo(TidalState state, String name, String[] artists) {

    public String info() {
        if (state != TidalState.PLAYING) {
            return "";
        }

        return name + " by " + String.join(", ", artists);
    }
}
