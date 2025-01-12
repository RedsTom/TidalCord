package fr.redstom.tidalcord.utils;

/**
 * A simple pair of two objects also known as a tuple
 *
 * @param left The left object
 * @param right The right object
 * @param <T> The type of the left object
 * @param <U> The type of the right object
 */
public record Pair<T, U>(T left, U right) {}
