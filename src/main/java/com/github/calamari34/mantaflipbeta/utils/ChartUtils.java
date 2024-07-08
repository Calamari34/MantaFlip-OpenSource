package com.github.calamari34.mantaflipbeta.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChartUtils {

    public static File createProfitGraph(List<Long> timeIntervals, List<Double> profitValues) {
        int width = 800;
        int height = 600;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set background color
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Set axis color
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        // Draw axes
        g2d.drawLine(50, 50, 50, height - 50); // Y-axis
        g2d.drawLine(50, height - 50, width - 50, height - 50); // X-axis

        // Set font for labels
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();

        // Draw labels
        g2d.drawString("Time (minutes)", (width - fm.stringWidth("Time (minutes)")) / 2, height - 10);
        g2d.drawString("Profit", 10, 30);

        // Plot the data points
        int maxTime = timeIntervals.stream().mapToInt(Long::intValue).max().orElse(1);
        double maxProfit = profitValues.stream().mapToDouble(Double::doubleValue).max().orElse(1);

        g2d.setColor(Color.BLUE);
        for (int i = 1; i < timeIntervals.size(); i++) {
            int x1 = 50 + (int) ((timeIntervals.get(i - 1) * (width - 100)) / maxTime);
            int y1 = height - 50 - (int) ((profitValues.get(i - 1) * (height - 100)) / maxProfit);
            int x2 = 50 + (int) ((timeIntervals.get(i) * (width - 100)) / maxTime);
            int y2 = height - 50 - (int) ((profitValues.get(i) * (height - 100)) / maxProfit);

            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();

        File imageFile = new File("profit_chart.png");
        try {
            ImageIO.write(bufferedImage, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }
}
