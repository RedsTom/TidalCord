package fr.redstom.tidalcord.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class ImageUtils {

    public static Image image(Object instance, String path, int width, int height) {
        try {
            Image img = Toolkit.getDefaultToolkit().createImage(ImageUtils.class.getResource(path))
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);

            return img;
        } catch (Exception e) {
            return null;
        }
    }

}
