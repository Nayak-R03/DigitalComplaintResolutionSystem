package com.dcms.ui;

import javax.swing.*;
import com.dcms.home.HomePage;
import java.awt.*;
import java.awt.event.*;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
public class UserLoginChoice extends JFrame {
    private JButton registerBtn, loginBtn, backBtn;

    public UserLoginChoice() {
        setTitle("User Options - DCMS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());

        // === Fonts and Colors ===
        Font titleFont = new Font("Verdana", Font.BOLD, 32);
        Font quoteFont = new Font("Georgia", Font.ITALIC, 18);
        Font btnFont = new Font("Arial", Font.BOLD, 16);
        Color titleColor = Color.decode("#1f416b");
        Color quoteColor = Color.decode("#0097b2");
        Color btnColor = Color.decode("#237ea6");

        // === Title ===
        JLabel titleLabel = new JLabel("User Panel", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(titleColor);

        // === Updated Quote ===
        JLabel quoteLabel = new JLabel("\"Access your account or create a new one\"", JLabel.CENTER);
        quoteLabel.setFont(quoteFont);
        quoteLabel.setForeground(quoteColor);

        // === Buttons ===
        registerBtn = new JButton("Register");
        loginBtn = new JButton("Login");
        backBtn = new JButton("Back");

        Dimension btnSize = new Dimension(160, 45);
        for (JButton btn : new JButton[]{registerBtn, loginBtn, backBtn}) {
            btn.setPreferredSize(btnSize);
            btn.setFont(btnFont);
            btn.setBackground(btnColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        // === Center Panel with GridBagLayout ===
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(titleLabel, gbc);

        gbc.gridy++;
        centerPanel.add(quoteLabel, gbc);

        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerBtn);
        buttonPanel.add(loginBtn);
        buttonPanel.add(backBtn);
        centerPanel.add(buttonPanel, gbc);

        // === Add centerPanel to mainPanel ===
        mainPanel.add(centerPanel, new GridBagConstraints());
        setContentPane(mainPanel);

        // === Button Actions ===
        registerBtn.addActionListener(e -> {
            dispose();
            new UserRegistration();
        });

        loginBtn.addActionListener(e -> {
            dispose();
            new UserLogin();
        });

        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        setVisible(true);
    }

    // === Gradient Background Panel ===
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            // Define 3 color stops
            float[] fractions = {0.0f, 0.5f, 1.0f};
            Color[] colors = {
                new Color(0, 199, 218),     // Cyan Blue
                new Color(149, 213, 178),   // Soft Green
                new Color(250, 222, 102)    // Warm Yellow
            };

            LinearGradientPaint gradient = new LinearGradientPaint(
                0, 0, width, 0,           // x1,y1 to x2,y2 (left to right)
                fractions, colors,
                MultipleGradientPaint.CycleMethod.NO_CYCLE
            );

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    public static void main(String[] args) {
        new UserLoginChoice();
    }
}
