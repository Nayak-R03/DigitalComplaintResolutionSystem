package com.dcms.ui;

import com.dcms.database.DBConnection;
import com.dcms.home.HomePage;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn, backBtn;

    public UserLogin() {
        setTitle("User Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 28));
        titleLabel.setForeground(Color.decode("#1f416b"));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        userLabel.setForeground(Color.decode("#0097b2"));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passLabel.setForeground(Color.decode("#0097b2"));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        loginBtn = new JButton("Login");
        backBtn = new JButton("Back");

        Color buttonColor = Color.decode("#237ea6");
        Font btnFont = new Font("Arial", Font.BOLD, 16);
        Dimension btnSize = new Dimension(120, 40);

        for (JButton btn : new JButton[]{loginBtn, backBtn}) {
            btn.setBackground(buttonColor);
            btn.setForeground(Color.WHITE);
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            btn.setPreferredSize(btnSize);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(backBtn, gbc);

        gbc.gridx = 1;
        panel.add(loginBtn, gbc);

        add(panel);
        setVisible(true);

        // === Button actions ===
        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        loginBtn.addActionListener(e -> authenticateUser());
    }

    // === Actual login logic ===
    private void authenticateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT user_id FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new UserDashboard(userId);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // === Gradient background panel ===
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
        new UserLogin();
    }
}
