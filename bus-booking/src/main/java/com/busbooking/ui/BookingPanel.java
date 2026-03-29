package com.busbooking.ui;

import com.busbooking.db.DatabaseConnection;
import com.busbooking.model.Booking;
import com.busbooking.model.Route;
import com.busbooking.model.User;
import com.busbooking.mpesa.MpesaService;
import com.busbooking.util.ReceiptGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingPanel extends JPanel {
    private final MainFrame mainFrame;
    private JComboBox<String> originBox, destBox;
    private JComboBox<Route> routeBox;
    private JSpinner travelDateSpinner;
    private SeatSelectionPanel seatPanel;
    private JPanel seatContainer;
    private final String[] cities = {"Nairobi", "Mombasa", "Kisumu", "Nakuru", "Eldoret", "Thika", "Nyeri", "Malindi"};

    public BookingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        JLabel welcomeLabel = new JLabel();
        if (mainFrame.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + mainFrame.getCurrentUser().getFullName() + "  ");
        }
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        welcomeLabel.setForeground(Color.WHITE);
        topBar.add(welcomeLabel);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            mainFrame.setCurrentUser(null);
            mainFrame.showPanel("login");
        });
        topBar.add(logoutBtn);

        JButton myBookingsBtn = new JButton("My Bookings");
        myBookingsBtn.setBackground(new Color(33, 150, 243));
        myBookingsBtn.setForeground(Color.WHITE);
        myBookingsBtn.setFocusPainted(false);
        myBookingsBtn.addActionListener(e -> showMyBookings());
        topBar.add(myBookingsBtn);

        add(topBar, BorderLayout.NORTH);

        // Center form
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(new Color(255, 255, 255, 210));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 100, 0), 2, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel header = new JLabel("🎫 Book Your Ticket", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setForeground(new Color(0, 100, 0));
        formCard.add(header, gbc);

        gbc.gridwidth = 1;

        // Origin
        gbc.gridy = 1; gbc.gridx = 0;
        formCard.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        originBox = new JComboBox<>(cities);
        formCard.add(originBox, gbc);

        // Destination
        gbc.gridy = 2; gbc.gridx = 0;
        formCard.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        destBox = new JComboBox<>(cities);
        destBox.setSelectedIndex(1);
        formCard.add(destBox, gbc);

        // Travel date
        gbc.gridy = 3; gbc.gridx = 0;
        formCard.add(new JLabel("Travel Date:"), gbc);
        gbc.gridx = 1;
        travelDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(travelDateSpinner, "yyyy-MM-dd");
        travelDateSpinner.setEditor(dateEditor);
        formCard.add(travelDateSpinner, gbc);

        // Search button
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton searchBtn = new JButton("🔍 Search Routes");
        searchBtn.setBackground(new Color(255, 152, 0));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 13));
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> searchRoutes());
        formCard.add(searchBtn, gbc);

        // Route selection
        gbc.gridy = 5;
        routeBox = new JComboBox<>();
        formCard.add(routeBox, gbc);

        // Select seat button
        gbc.gridy = 6;
        JButton selectSeatBtn = new JButton("🪑 Select Seat");
        selectSeatBtn.setBackground(new Color(0, 100, 0));
        selectSeatBtn.setForeground(Color.WHITE);
        selectSeatBtn.setFont(new Font("Arial", Font.BOLD, 13));
        selectSeatBtn.setFocusPainted(false);
        selectSeatBtn.addActionListener(e -> showSeats());
        formCard.add(selectSeatBtn, gbc);

        // Seat panel container
        gbc.gridy = 7;
        seatContainer = new JPanel(new BorderLayout());
        seatContainer.setOpaque(false);
        seatContainer.setPreferredSize(new Dimension(300, 400));
        formCard.add(seatContainer, gbc);

        // Book & Pay button
        gbc.gridy = 8;
        JButton bookBtn = new JButton("💳 Pay with M-Pesa & Book");
        bookBtn.setBackground(new Color(76, 175, 80));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));
        bookBtn.setFocusPainted(false);
        bookBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> handleBooking());
        formCard.add(bookBtn, gbc);

        JScrollPane scrollForm = new JScrollPane(formCard);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        scrollForm.setBorder(null);
        add(scrollForm, BorderLayout.CENTER);
    }

    private void searchRoutes() {
        String origin = (String) originBox.getSelectedItem();
        String dest = (String) destBox.getSelectedItem();

        if (origin.equals(dest)) {
            JOptionPane.showMessageDialog(this, "Origin and destination cannot be the same.");
            return;
        }

        routeBox.removeAllItems();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT r.*, bc.name as bus_name, bc.total_seats FROM routes r " +
                    "JOIN bus_companies bc ON r.bus_company_id = bc.id " +
                    "WHERE r.origin = ? AND r.destination = ?");
            ps.setString(1, origin);
            ps.setString(2, dest);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                Route route = new Route();
                route.setId(rs.getInt("id"));
                route.setOrigin(rs.getString("origin"));
                route.setDestination(rs.getString("destination"));
                route.setPrice(rs.getBigDecimal("price"));
                route.setBusCompanyId(rs.getInt("bus_company_id"));
                route.setBusName(rs.getString("bus_name"));
                route.setTotalSeats(rs.getInt("total_seats"));
                route.setDepartureTime(rs.getString("departure_time"));
                routeBox.addItem(route);
                found = true;
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No routes found for this journey.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showSeats() {
        Route route = (Route) routeBox.getSelectedItem();
        if (route == null) {
            JOptionPane.showMessageDialog(this, "Please search and select a route first.");
            return;
        }

        java.util.Date date = (java.util.Date) travelDateSpinner.getValue();
        LocalDate travelDate = new java.sql.Date(date.getTime()).toLocalDate();

        seatContainer.removeAll();
        seatPanel = new SeatSelectionPanel(route.getTotalSeats(), route.getId(), travelDate);
        seatContainer.add(seatPanel, BorderLayout.CENTER);
        seatContainer.revalidate();
        seatContainer.repaint();
    }

    private void handleBooking() {
        User user = mainFrame.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Please login first.");
            mainFrame.showPanel("login");
            return;
        }

        Route route = (Route) routeBox.getSelectedItem();
        if (route == null) {
            JOptionPane.showMessageDialog(this, "Please select a route.");
            return;
        }

        if (seatPanel == null || seatPanel.getSelectedSeat() < 1) {
            JOptionPane.showMessageDialog(this, "Please select a seat.");
            return;
        }

        int seat = seatPanel.getSelectedSeat();
        java.util.Date date = (java.util.Date) travelDateSpinner.getValue();
        LocalDate travelDate = new java.sql.Date(date.getTime()).toLocalDate();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm booking?\n\n" +
                "Bus: " + route.getBusName() + "\n" +
                "Route: " + route.getOrigin() + " → " + route.getDestination() + "\n" +
                "Seat: " + seat + "\n" +
                "Date: " + travelDate + "\n" +
                "Amount: KES " + route.getPrice() + "\n\n" +
                "M-Pesa STK Push will be sent to: " + user.getPhone(),
                "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Attempt M-Pesa STK Push
        String mpesaCode = "SIM-" + System.currentTimeMillis(); // Fallback code
        try {
            String response = MpesaService.initiateSTKPush(
                    user.getPhone(), route.getPrice().intValue(),
                    "BUS-" + route.getId() + "-S" + seat);
            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            if (json.has("CheckoutRequestID")) {
                JOptionPane.showMessageDialog(this,
                        "STK Push sent to " + user.getPhone() + "!\nPlease enter your M-Pesa PIN on your phone.\n\nCheckout ID: " +
                        json.get("CheckoutRequestID").getAsString(),
                        "M-Pesa", JOptionPane.INFORMATION_MESSAGE);
                mpesaCode = json.get("CheckoutRequestID").getAsString();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "M-Pesa STK Push failed (using simulation).\nError: " + ex.getMessage() +
                    "\n\nBooking will proceed with simulated payment.",
                    "M-Pesa Notice", JOptionPane.WARNING_MESSAGE);
        }

        // Save booking
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, route_id, seat_number, booking_date, travel_date, mpesa_code, phone_paid, amount_paid, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, user.getId());
            ps.setInt(2, route.getId());
            ps.setInt(3, seat);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.setDate(5, Date.valueOf(travelDate));
            ps.setString(6, mpesaCode);
            ps.setString(7, user.getPhone());
            ps.setBigDecimal(8, route.getPrice());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int bookingId = 0;
            if (keys.next()) bookingId = keys.getInt(1);

            // Generate receipt
            Booking booking = new Booking();
            booking.setId(bookingId);
            booking.setPassengerName(user.getFullName());
            booking.setBusName(route.getBusName());
            booking.setOrigin(route.getOrigin());
            booking.setDestination(route.getDestination());
            booking.setSeatNumber(seat);
            booking.setTravelDate(travelDate);
            booking.setAmountPaid(route.getPrice());
            booking.setMpesaCode(mpesaCode);
            booking.setStatus("CONFIRMED");

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("Receipt_BK" + bookingId + ".pdf"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = ReceiptGenerator.generateReceipt(booking, chooser.getSelectedFile().getAbsolutePath());
                if (path != null) {
                    JOptionPane.showMessageDialog(this, "Booking confirmed! Receipt saved to:\n" + path,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Booking confirmed! Booking ID: BK-" + String.format("%05d", bookingId),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            // Refresh seats
            showSeats();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "This seat is already booked!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMyBookings() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT b.*, r.origin, r.destination, bc.name as bus_name FROM bookings b " +
                    "JOIN routes r ON b.route_id = r.id JOIN bus_companies bc ON r.bus_company_id = bc.id " +
                    "WHERE b.user_id = ? ORDER BY b.created_at DESC");
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder("<html><table border='1' cellpadding='5'>");
            sb.append("<tr><th>ID</th><th>Bus</th><th>Route</th><th>Seat</th><th>Date</th><th>Amount</th><th>Status</th></tr>");
            boolean found = false;
            while (rs.next()) {
                found = true;
                sb.append("<tr><td>BK-").append(String.format("%05d", rs.getInt("id"))).append("</td>")
                  .append("<td>").append(rs.getString("bus_name")).append("</td>")
                  .append("<td>").append(rs.getString("origin")).append("→").append(rs.getString("destination")).append("</td>")
                  .append("<td>").append(rs.getInt("seat_number")).append("</td>")
                  .append("<td>").append(rs.getDate("travel_date")).append("</td>")
                  .append("<td>KES ").append(rs.getBigDecimal("amount_paid")).append("</td>")
                  .append("<td>").append(rs.getString("status")).append("</td></tr>");
            }
            sb.append("</table></html>");

            if (!found) {
                JOptionPane.showMessageDialog(this, "No bookings found.");
            } else {
                JLabel label = new JLabel(sb.toString());
                JScrollPane sp = new JScrollPane(label);
                sp.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(this, sp, "My Bookings", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
