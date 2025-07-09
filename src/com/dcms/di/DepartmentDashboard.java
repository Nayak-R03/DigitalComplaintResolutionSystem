package com.dcms.di;

import com.dcms.database.DBConnection;
import com.dcms.home.HomePage;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DepartmentDashboard extends JFrame {
    private int departmentId;
    private JTable complaintTable;
    private DefaultTableModel model;

    public DepartmentDashboard(int departmentId) {
        this.departmentId = departmentId;

        setTitle("Department Dashboard - DCMS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("Department Complaints", JLabel.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Complaints Assigned to Your Department"));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "User", "Title", "Complaint", "Status", "Resolution", "Resolve"});

        complaintTable = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only "Resolve" column is editable (for button)
            }
        };
        complaintTable.setRowHeight(30);
        complaintTable.setFont(new Font("Arial", Font.PLAIN, 14));
        complaintTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        complaintTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane tableScroll = new JScrollPane(complaintTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(1000, 400));

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Home");
        backButton.setPreferredSize(new Dimension(160, 40));
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(Color.decode("#237ea6"));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadComplaints();

        // Event Handlers
        backButton.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        complaintTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = complaintTable.rowAtPoint(e.getPoint());
                int col = complaintTable.columnAtPoint(e.getPoint());
                if (col == 6 && row != -1) {
                    int complaintId = (int) model.getValueAt(row, 0);
                    if (model.getValueAt(row, 4).equals("Resolved")) {
                        JOptionPane.showMessageDialog(null, "This complaint is already resolved.");
                        return;
                    }

                    JTextArea resolutionArea = new JTextArea(5, 30);
                    resolutionArea.setLineWrap(true);
                    resolutionArea.setWrapStyleWord(true);
                    JScrollPane scrollPane = new JScrollPane(resolutionArea);

                    int option = JOptionPane.showConfirmDialog(null, scrollPane, "Enter Resolution", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        String resolution = resolutionArea.getText().trim();
                        if (!resolution.isEmpty()) {
                            markAsResolved(complaintId, resolution);
                        } else {
                            JOptionPane.showMessageDialog(null, "Resolution cannot be empty.");
                        }
                    }
                }
            }
        });

        setVisible(true);
    }

    private void loadComplaints() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.complaint_id, u.username, c.title, c.complaint_text, c.status, c.resolution " +
                         "FROM complaints c JOIN users u ON c.user_id = u.user_id " +
                         "WHERE c.dept_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, departmentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("complaint_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getString("complaint_text"),
                        rs.getString("status"),
                        rs.getString("resolution"),
                        "Resolve"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void markAsResolved(int complaintId, String resolution) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE complaints SET status='Resolved', resolution=? WHERE complaint_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, resolution);
            pst.setInt(2, complaintId);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Complaint marked as resolved.");
                loadComplaints();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Background Gradient Panel
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
        new DepartmentDashboard(1); // Example for department ID 1
    }
}
