package fr.redstom.tidalcord.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ImageUtils {

    public static Image image(String path, int width, int height) {
        Image img = Toolkit.getDefaultToolkit().createImage(ImageUtils.class.getResource(path))
                .getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return img;
    }

    public static ImageIcon icon(String path, int width, int height) {
        Image img = ImageUtils.image(path, width, height);

        return new ImageIcon(img);
    }

}
