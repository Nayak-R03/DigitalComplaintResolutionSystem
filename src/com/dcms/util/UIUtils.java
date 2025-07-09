package com.dcms.util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static Font headerFont() {
        return new Font("Arial", Font.BOLD, 22);
    }

    public static Font textFont() {
        return new Font("Segoe UI", Font.PLAIN, 14);
    }

    public static Color backgroundColor() {
        return new Color(240, 248, 255); // Light blue background
    }

    public static Color buttonColor() {
        return new Color(70, 130, 180); // Steel blue
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor());
        button.setForeground(Color.WHITE);
        button.setFont(textFont());
        button.setFocusPainted(false);
        return button;
    }

    public static JPanel createBackgroundPanel(Image backgroundImage) {
        return new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }
}

