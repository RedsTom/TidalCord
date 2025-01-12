package fr.redstom.tidalcord.utils;

import java.awt.*;

import javax.swing.*;

/** A utility class for loading images */
public class ImageUtils {

    /**
     * Loads an image from the resources folder and scales it to the specified width and height
     *
     * @param path The path to the image
     * @param width The width of the image
     * @param height The height of the image
     * @return The loaded and scaled image
     */
    public static Image image(String path, int width, int height) {
        Image img =
                Toolkit.getDefaultToolkit()
                        .createImage(ImageUtils.class.getResource(normalize(path)))
                        .getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return img;
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
