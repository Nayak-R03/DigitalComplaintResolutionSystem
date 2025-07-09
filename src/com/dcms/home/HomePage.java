package com.dcms.home;

import javax.swing.*;
import com.dcms.ai.AdminLogin;
import com.dcms.di.DepartmentLogin;
import com.dcms.ui.UserLoginChoice;
import java.awt.*;
import java.awt.event.*;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;


public class HomePage extends JFrame {
    private JButton userLoginBtn, adminLoginBtn, deptLoginBtn;

    public HomePage() {
        setTitle("Digital Complaint Resolution System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen

        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setLayout(new GridBagLayout()); // Center layout
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        // === Title ===
        JLabel titleLabel = new JLabel("Digital Complaint Resolution System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 32));
        titleLabel.setForeground(Color.decode("#3533cd")); // Title color

        // === Quote ===
        JLabel quoteLabel = new JLabel("\"Your Voice, Our Response\"", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Georgia", Font.ITALIC, 18));
        quoteLabel.setForeground(Color.decode("#0097b2")); // Quote color

        // === Buttons ===
        userLoginBtn = new JButton("User");
        adminLoginBtn = new JButton("Admin");
        deptLoginBtn = new JButton("Department");

        Dimension buttonSize = new Dimension(160, 45);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Color buttonColor = Color.decode("#5271ff"); // Button color

        for (JButton btn : new JButton[]{userLoginBtn, adminLoginBtn, deptLoginBtn}) {
            btn.setPreferredSize(buttonSize);
            btn.setFont(buttonFont);
            btn.setBackground(buttonColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel);
        topPanel.add(quoteLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 40));
        buttonPanel.add(userLoginBtn);
        buttonPanel.add(adminLoginBtn);
        buttonPanel.add(deptLoginBtn);

        // === Center Everything in One Panel ===
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(topPanel);
        centerPanel.add(Box.createVerticalStrut(40)); // spacing between title and buttons
        centerPanel.add(buttonPanel);

        // === Center align in GridBagLayout ===
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(centerPanel, gbc);

        setContentPane(backgroundPanel);

        // === Button Actions ===
        userLoginBtn.addActionListener(e -> {
            dispose();
            new UserLoginChoice();
        });

        adminLoginBtn.addActionListener(e -> {
            dispose();
            new AdminLogin();
        });

        deptLoginBtn.addActionListener(e -> {
            dispose();
            new DepartmentLogin();
        });

        setVisible(true);
    }

    // === Inner class for 4-color vertical gradient background ===
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
        new HomePage();
    }
}
