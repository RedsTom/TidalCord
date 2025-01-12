package fr.redstom.tidalcord.data;

public enum TidalState {
    CLOSED(1),
    OPENED(2),
    PLAYING(3),
    ERROR(4);

    private final int importance;

    TidalState(int importance) {
        this.importance = importance;
    }

    public int importance() {
        return importance;
    }
}
