package fr.redstom.tidalcord.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Watcher<T> {

    private T value;
    private List<Consumer<T>> listeners;

    public Watcher(T value) {
        this.value = value;
        this.listeners = new LinkedList<>();
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        if(this.value.equals(value)) return;

        this.value = value;
        listeners.forEach(watcher -> watcher.accept(value));
    }

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
        listener.accept(value);
    }

    public void setIf(T tidalProcessInfo, BiFunction<T, T, Boolean> o) {
        if (o.apply(tidalProcessInfo, value)) {
            set(tidalProcessInfo);
        }
    }
}
