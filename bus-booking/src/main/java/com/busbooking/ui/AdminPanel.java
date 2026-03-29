package com.busbooking.ui;

import com.busbooking.db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable bookingsTable, usersTable;
    private DefaultTableModel bookingsModel, usersModel;

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.BETWEEN));
        topBar.setOpaque(false);
        JLabel title = new JLabel("🛡️ Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        topBar.add(title);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(new Color(33, 150, 243));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadData());
        topBar.add(refreshBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> mainFrame.showPanel("login"));
        topBar.add(logoutBtn);

        add(topBar, BorderLayout.NORTH);

        // Tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        // Bookings tab
        JPanel bookingsTab = new JPanel(new BorderLayout(5, 5));
        bookingsModel = new DefaultTableModel(
                new String[]{"ID", "Passenger", "Bus", "Route", "Seat", "Travel Date", "Amount", "M-Pesa", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingsTable = new JTable(bookingsModel);
        bookingsTable.setRowHeight(25);
        bookingsTab.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        JPanel bookingActions = new JPanel(new FlowLayout());
        JButton deleteBtn = new JButton("🗑️ Delete Booking");
        deleteBtn.setBackground(new Color(244, 67, 54));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteBooking());
        bookingActions.add(deleteBtn);

        JButton cancelBtn = new JButton("❌ Cancel Booking");
        cancelBtn.setBackground(new Color(255, 152, 0));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> cancelBooking());
        bookingActions.add(cancelBtn);

        bookingsTab.add(bookingActions, BorderLayout.SOUTH);
        tabs.addTab("📋 Bookings", bookingsTab);

        // Users tab
        JPanel usersTab = new JPanel(new BorderLayout(5, 5));
        usersModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Email", "Phone", "Created"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(usersModel);
        usersTable.setRowHeight(25);
        usersTab.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        tabs.addTab("👥 Passengers", usersTab);

        // Stats tab
        JPanel statsTab = new JPanel(new GridBagLayout());
        statsTab.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel statsTitle = new JLabel("📊 Dashboard Statistics", SwingConstants.CENTER);
        statsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        statsTab.add(statsTitle, gbc);

        // Stats will be loaded dynamically
        gbc.gridy = 1;
        JLabel statsContent = new JLabel();
        statsContent.setFont(new Font("Arial", Font.PLAIN, 14));
        statsTab.add(statsContent, gbc);

        // Load stats
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs1 = st.executeQuery("SELECT COUNT(*) as cnt FROM users");
                rs1.next(); int totalUsers = rs1.getInt("cnt");

                rs1 = st.executeQuery("SELECT COUNT(*) as cnt FROM bookings");
                rs1.next(); int totalBookings = rs1.getInt("cnt");

                rs1 = st.executeQuery("SELECT COALESCE(SUM(amount_paid),0) as total FROM bookings WHERE status='CONFIRMED'");
                rs1.next(); double totalRevenue = rs1.getDouble("total");

                rs1 = st.executeQuery("SELECT COUNT(*) as cnt FROM bookings WHERE status='CONFIRMED'");
                rs1.next(); int confirmed = rs1.getInt("cnt");

                rs1 = st.executeQuery("SELECT COUNT(*) as cnt FROM bookings WHERE status='CANCELLED'");
                rs1.next(); int cancelled = rs1.getInt("cnt");

                statsContent.setText(String.format(
                        "<html><div style='font-size:14px;'>" +
                        "👥 Total Passengers: <b>%d</b><br><br>" +
                        "📋 Total Bookings: <b>%d</b><br><br>" +
                        "✅ Confirmed: <b>%d</b><br><br>" +
                        "❌ Cancelled: <b>%d</b><br><br>" +
                        "💰 Total Revenue: <b>KES %,.2f</b>" +
                        "</div></html>",
                        totalUsers, totalBookings, confirmed, cancelled, totalRevenue));
            } catch (SQLException ex) {
                statsContent.setText("Error loading stats: " + ex.getMessage());
            }
        });

        tabs.addTab("📊 Statistics", statsTab);

        add(tabs, BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        // Load bookings
        bookingsModel.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT b.*, u.full_name, r.origin, r.destination, bc.name as bus_name FROM bookings b " +
                    "JOIN users u ON b.user_id = u.id JOIN routes r ON b.route_id = r.id " +
                    "JOIN bus_companies bc ON r.bus_company_id = bc.id ORDER BY b.created_at DESC");
            while (rs.next()) {
                bookingsModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("full_name"), rs.getString("bus_name"),
                        rs.getString("origin") + " → " + rs.getString("destination"),
                        rs.getInt("seat_number"), rs.getDate("travel_date"),
                        "KES " + rs.getBigDecimal("amount_paid"), rs.getString("mpesa_code"),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }

        // Load users
        usersModel.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users ORDER BY created_at DESC");
            while (rs.next()) {
                usersModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("full_name"), rs.getString("email"),
                        rs.getString("phone"), rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void deleteBooking() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booking to delete."); return; }

        int id = (int) bookingsModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete booking #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM bookings WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(this, "Booking deleted.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void cancelBooking() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booking."); return; }

        int id = (int) bookingsModel.getValueAt(row, 0);
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = 'CANCELLED' WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(this, "Booking cancelled.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
