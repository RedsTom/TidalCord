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
     * Set the watched value if it is different from the current value
     *
     * @param value
     */
    public void setCheck(T value) {
        if ((value == null && this.value == null)
        || (value != null && value.equals(this.value))
        || (this.value != null && this.value.equals(value))) {
            return;
        }

        set(value);
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
