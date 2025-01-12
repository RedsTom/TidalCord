package fr.redstom.tidalcord.utils;

import java.awt.*;

import javax.swing.*;

public class ImageUtils {

    public static Image image(String path, int width, int height) {
        Image img =
                Toolkit.getDefaultToolkit()
                        .createImage(ImageUtils.class.getResource(normalize(path)))
                        .getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return img;
    }

    public static ImageIcon icon(String path, int width, int height) {
        Image img = ImageUtils.image(path, width, height);

        return new ImageIcon(img);
    }

    private static String normalize(String path) {
        if (!path.startsWith("/")) {
            return "/" + path;
        }

        return path;
    }
}
