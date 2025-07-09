package com.dcms.ai;

import com.dcms.database.DBConnection;
import com.dcms.home.HomePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn, backBtn;

    public AdminLogin() {
        setTitle("Admin Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("Admin Login");
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

        // Event Handling
        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        loginBtn.addActionListener(e -> checkLogin());
    }

    private void checkLogin() {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new AdminDashboard(); // open Admin dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Try again.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred: " + ex.getMessage());
        }
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(255, 245, 183);  // Light Yellow (#FFF5B7)
            Color color2 = new Color(255, 182, 240);  // Light Pink   (#FFB6F0)
            GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static void main(String[] args) {
        new AdminLogin();
    }
}
