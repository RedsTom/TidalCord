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

import java.awt.*;

import javax.swing.*;

/** A utility class for loading images */
public class ImageUtils {

    /**
     * Loads an image from the resources folder
     *
     * @param path The path to the image
     * @return The loaded image
     */
    private static Image image(String path) {
        return Toolkit.getDefaultToolkit().createImage(ImageUtils.class.getResource(normalize(path)));
    }

    /**
     * Loads an image from the resources folder and scales it to the specified width and height
     *
     * @param path The path to the image
     * @param width The width of the image
     * @param height The height of the image
     * @return The loaded and scaled image
     */
    public static Image image(String path, int width, int height) {
        return image(path).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /**
     * Loads an image from the resources folder and return it as an ImageIcon
     *
     * @param path The path to the image
     * @return The loaded image as an ImageIcon
     */
    public static ImageIcon icon(String path) {
        Image img = ImageUtils.image(path);

        return new ImageIcon(img);
    }

    /**
     * Loads an image from the resources folder and return it as an ImageIcon
     *
     * @param path The path to the image
     * @param width The width of the image
     * @param height The height of the image
     * @return The loaded image as an ImageIcon
     */
    public static ImageIcon icon(String path, int width, int height) {
        Image img = ImageUtils.image(path, width, height);

        return new ImageIcon(img);
    }

    /**
     * Normalizes the path to the image
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    private static String normalize(String path) {
        if (!path.startsWith("/")) {
            return "/" + path;
        }

        return path;
    }
}
