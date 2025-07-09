package com.dcms.ui;

import com.dcms.database.DBConnection;
import com.dcms.home.HomePage;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserDashboard extends JFrame {
    private int userId;
    private JComboBox<String> departmentBox;
    private JTextField titleField;
    private JTextArea complaintArea;
    private JButton submitBtn, backBtn;
    private JTable complaintTable;
    private DefaultTableModel model;

    public UserDashboard(int userId) {
        this.userId = userId;

        setTitle("User Dashboard - Digital Complaint System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // === Complaint Form ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Submit New Complaint"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel deptLabel = new JLabel("Select Department:");
        JLabel titleLabel = new JLabel("Title:");
        JLabel complaintLabel = new JLabel("Complaint:");

        Color labelColor = Color.WHITE;
        deptLabel.setForeground(labelColor);
        titleLabel.setForeground(labelColor);
        complaintLabel.setForeground(labelColor);
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        deptLabel.setFont(labelFont);
        titleLabel.setFont(labelFont);
        complaintLabel.setFont(labelFont);

        departmentBox = new JComboBox<>();
        loadDepartments();
        departmentBox.setPreferredSize(new Dimension(300, 25));
        departmentBox.setBackground(Color.decode("#d0e4f7"));
        departmentBox.setFont(new Font("Arial", Font.PLAIN, 14));

        titleField = new JTextField();
        titleField.setPreferredSize(new Dimension(300, 25));
        titleField.setBackground(Color.decode("#d0e4f7"));
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));

        complaintArea = new JTextArea(4, 40);
        complaintArea.setLineWrap(true);
        complaintArea.setWrapStyleWord(true);
        complaintArea.setFont(new Font("Arial", Font.PLAIN, 14));
        complaintArea.setBackground(Color.decode("#d0e4f7"));
        JScrollPane complaintScroll = new JScrollPane(complaintArea);

        submitBtn = new JButton("Submit Complaint");
        backBtn = new JButton("Back to Home");

        Color buttonColor = Color.decode("#237ea6");
        for (JButton btn : new JButton[]{submitBtn, backBtn}) {
            btn.setBackground(buttonColor);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(180, 40));
            btn.setFocusPainted(false);
        }

        // Center title
        JLabel titleHeading = new JLabel("Submit New Complaint", JLabel.CENTER);
        titleHeading.setFont(new Font("Verdana", Font.BOLD, 24));
        titleHeading.setForeground(new Color(255, 255, 255));
        titleHeading.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        mainPanel.add(titleHeading, BorderLayout.NORTH);

        // Add form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(deptLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(departmentBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(complaintLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(complaintScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(backBtn, gbc);
        gbc.gridx = 1;
        formPanel.add(submitBtn, gbc);

        // === Complaint Table ===
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Title", "Status", "Resolution", "View"});
        complaintTable = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        complaintTable.setRowHeight(30);
        complaintTable.setFont(new Font("Arial", Font.PLAIN, 14));
        complaintTable.setBackground(Color.decode("#d0e4f7"));
        complaintTable.setSelectionBackground(Color.decode("#a9c9ea"));

        JScrollPane tableScroll = new JScrollPane(complaintTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Your Previous Complaints"));

        // === Add to main panel ===
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);
        centerPanel.add(tableScroll);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Load complaints
        loadComplaints();

        // Actions
        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        submitBtn.addActionListener(e -> submitComplaint());

        complaintTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = complaintTable.rowAtPoint(e.getPoint());
                int col = complaintTable.columnAtPoint(e.getPoint());
                if (col == 4 && row != -1) {
                    String complaintText = getComplaintText((int) model.getValueAt(row, 0));
                    JTextArea viewArea = new JTextArea(complaintText);
                    viewArea.setLineWrap(true);
                    viewArea.setWrapStyleWord(true);
                    viewArea.setEditable(false);
                    viewArea.setBackground(Color.WHITE);
                    viewArea.setFont(new Font("Arial", Font.PLAIN, 14));
                    JScrollPane scroll = new JScrollPane(viewArea);
                    scroll.setPreferredSize(new Dimension(500, 200));
                    JOptionPane.showMessageDialog(UserDashboard.this, scroll, "Complaint Details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private void loadDepartments() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT dept_name FROM departments";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                departmentBox.addItem(rs.getString("dept_name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void submitComplaint() {
        String dept = (String) departmentBox.getSelectedItem();
        String title = titleField.getText().trim();
        String complaintText = complaintArea.getText().trim();

        if (dept == null || title.isEmpty() || complaintText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO complaints (user_id, dept_id, title, complaint_text, status) VALUES (?, (SELECT dept_id FROM departments WHERE dept_name = ?), ?, ?, 'Pending')";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setString(2, dept);
            pst.setString(3, title);
            pst.setString(4, complaintText);

            int inserted = pst.executeUpdate();
            if (inserted > 0) {
                JOptionPane.showMessageDialog(this, "Complaint submitted successfully!");
                titleField.setText("");
                complaintArea.setText("");
                loadComplaints();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadComplaints() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT complaint_id, title, status, resolution FROM complaints WHERE user_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("complaint_id"),
                        rs.getString("title"),
                        rs.getString("status"),
                        rs.getString("resolution"),
                        "View"
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getComplaintText(int complaintId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT complaint_text FROM complaints WHERE complaint_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, complaintId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("complaint_text");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Complaint text not found.";
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
        new UserDashboard(1); // For testing
    }
}
