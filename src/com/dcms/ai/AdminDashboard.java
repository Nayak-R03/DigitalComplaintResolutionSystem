package com.dcms.ai;

import com.dcms.database.DBConnection;
import com.dcms.home.HomePage;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTable complaintTable;
    private DefaultTableModel model;
    private JButton backButton;

    public AdminDashboard() {
        setTitle("Admin Dashboard - DCMS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // === Heading ===
        JLabel heading = new JLabel("All Complaints Overview", JLabel.CENTER);
        heading.setFont(new Font("Verdana", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        mainPanel.add(heading, BorderLayout.NORTH);

        // === Table Panel ===
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Complaint List"));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "User", "Department", "Title", "Status", "Resolution"});

        complaintTable = new JTable(model);
        complaintTable.setRowHeight(30);
        complaintTable.setFont(new Font("Arial", Font.PLAIN, 14));
        complaintTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        complaintTable.setSelectionBackground(new Color(173, 216, 230));
        complaintTable.setGridColor(Color.GRAY);
        complaintTable.setBackground(new Color(240, 250, 255));

        JScrollPane scrollPane = new JScrollPane(complaintTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // === Back Button ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        backButton = new JButton("Back to Home");
        backButton.setBackground(Color.decode("#237ea6"));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setPreferredSize(new Dimension(160, 40));
        backButton.setFocusPainted(false);
        bottomPanel.add(backButton);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        loadComplaints();

        // === Events ===
        backButton.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        setVisible(true);
    }

    private void loadComplaints() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.complaint_id, u.username, d.dept_name, c.title, c.status, c.resolution " +
                         "FROM complaints c " +
                         "JOIN users u ON c.user_id = u.user_id " +
                         "JOIN departments d ON c.dept_id = d.dept_id";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("complaint_id"),
                        rs.getString("username"),
                        rs.getString("dept_name"),
                        rs.getString("title"),
                        rs.getString("status"),
                        rs.getString("resolution")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // === Gradient Background ===
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
        new AdminDashboard();
    }
}
