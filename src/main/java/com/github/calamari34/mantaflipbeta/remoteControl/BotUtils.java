package com.github.calamari34.mantaflipbeta.remoteControl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class BotUtils {

    public static String takeScreenShot() {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            int random = (new Random()).nextInt(19900721);
            if (!(new File(System.getenv("TEMP") + "\\MantaFlip")).exists()) {
                boolean success = (new File(System.getenv("TEMP") + "\\MantaFlip")).mkdirs();
                if (!success) {
                    System.out.println("Failed to create directory");
                }
            }
            String path = System.getenv("TEMP") + "\\MantaFlip\\" + random + ".png";
            File file = new File(path);
            ImageIO.write(image, "png", file);
            return path;
        } catch (AWTException | IOException e) {
            e.printStackTrace();
            System.out.println("Failed to take screenshot");
        }
        return null;
    }
}