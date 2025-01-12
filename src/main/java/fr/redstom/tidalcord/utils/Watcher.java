package fr.redstom.tidalcord.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A class that watches a value and notifies listeners when it changes
 *
 * @param <T> The type of the value to watch
 */
public class Watcher<T> {

    private T value;
    private List<Consumer<T>> listeners;

    public Watcher(T value) {
        this.value = value;
        this.listeners = new LinkedList<>();
    }

    /**
     * Get the watched value
     *
     * @return The watched value
     */
    public T get() {
        return value;
    }

    /**
     * Set the watched value and notify listeners
     *
     * @param value The new value
     */
    public void set(T value) {
        this.value = value;
        listeners.forEach(watcher -> watcher.accept(value));
    }

    /**
     * Set the watched value if the condition is met
     *
     * @param o The condition to meet
     */
    public void setIf(T tidalProcessInfo, BiFunction<T, T, Boolean> o) {
        if (o.apply(tidalProcessInfo, value)) {
            set(tidalProcessInfo);
        }
    }

    /**
     * Add a listener to the watcher
     *
     * @param listener The listener to add
     */
    public void addListener(Consumer<T> listener) {
        addListener(listener, false);
    }

    /**
     * Add a listener to the watcher
     *
     * @param listener The listener to add
     * @param silent Whether to notify the listener immediately
     */
    public void addListener(Consumer<T> listener, boolean silent) {
        listeners.add(listener);
        if (!silent) {
            listener.accept(value);
        }
    }
}
