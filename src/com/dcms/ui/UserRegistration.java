package com.dcms.ui;

import com.dcms.home.HomePage;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserRegistration extends JFrame {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;

    public UserRegistration() {
        setTitle("User Registration - Digital Complaint System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel backgroundPanel = new GradientPanel();
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(80, 300, 80, 300));

        // Title
        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 30));
        titleLabel.setForeground(Color.decode("#1f416b"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Color labelColor = Color.decode("#0097b2");

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(labelColor);
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(labelColor);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(labelColor);
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(passwordField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        registerButton = new JButton("Register");
        backButton = new JButton("Back");

        for (JButton btn : new JButton[]{registerButton, backButton}) {
            btn.setPreferredSize(new Dimension(130, 40));
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setBackground(Color.decode("#237ea6"));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(backButton);

        formPanel.add(buttonPanel, gbc);

        backgroundPanel.add(titleLabel, BorderLayout.NORTH);
        backgroundPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);
        setVisible(true);

        // Button Actions
        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            dispose();
            new UserLoginChoice();
        });
    }

    private void registerUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try (java.sql.Connection conn = com.dcms.database.DBConnection.getConnection()) {
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, password);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
                new UserLoginChoice();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
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
        new UserRegistration();
    }
}
