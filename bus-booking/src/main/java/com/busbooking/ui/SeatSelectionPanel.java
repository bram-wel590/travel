package com.busbooking.ui;

import com.busbooking.db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class SeatSelectionPanel extends JPanel {
    private final int totalSeats;
    private final int routeId;
    private final LocalDate travelDate;
    private final Map<Integer, JToggleButton> seatButtons = new LinkedHashMap<>();
    private final Set<Integer> bookedSeats = new HashSet<>();
    private int selectedSeat = -1;

    public SeatSelectionPanel(int totalSeats, int routeId, LocalDate travelDate) {
        this.totalSeats = totalSeats;
        this.routeId = routeId;
        this.travelDate = travelDate;
        loadBookedSeats();
        buildLayout();
    }

    private void loadBookedSeats() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT seat_number FROM bookings WHERE route_id = ? AND travel_date = ? AND status != 'CANCELLED'");
            ps.setInt(1, routeId);
            ps.setDate(2, Date.valueOf(travelDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getInt("seat_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        legend.setOpaque(false);
        legend.add(createLegendItem(new Color(76, 175, 80), "Available"));
        legend.add(createLegendItem(new Color(244, 67, 54), "Booked"));
        legend.add(createLegendItem(new Color(33, 150, 243), "Selected"));
        add(legend, BorderLayout.NORTH);

        // Bus body panel
        JPanel busBody = new JPanel(new BorderLayout());
        busBody.setBackground(new Color(240, 240, 240));
        busBody.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Driver area
        JPanel driverArea = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        driverArea.setOpaque(false);
        JLabel driverLabel = new JLabel("🚗 DRIVER");
        driverLabel.setFont(new Font("Arial", Font.BOLD, 12));
        driverArea.add(driverLabel);
        busBody.add(driverArea, BorderLayout.NORTH);

        // Seats grid: 4 columns (2 | aisle | 2) with varying rows
        int rows = (int) Math.ceil(totalSeats / 4.0);
        JPanel seatsPanel = new JPanel(new GridBagLayout());
        seatsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        int seatNum = 1;
        for (int row = 0; row < rows && seatNum <= totalSeats; row++) {
            // Left side (2 seats)
            for (int col = 0; col < 2 && seatNum <= totalSeats; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
                JToggleButton btn = createSeatButton(seatNum);
                seatButtons.put(seatNum, btn);
                seatsPanel.add(btn, gbc);
                seatNum++;
            }

            // Aisle
            gbc.gridx = 2;
            gbc.gridy = row;
            JLabel aisle = new JLabel("  ");
            aisle.setPreferredSize(new Dimension(30, 35));
            seatsPanel.add(aisle, gbc);

            // Right side (2 seats)
            for (int col = 3; col < 5 && seatNum <= totalSeats; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
                JToggleButton btn = createSeatButton(seatNum);
                seatButtons.put(seatNum, btn);
                seatsPanel.add(btn, gbc);
                seatNum++;
            }
        }

        JScrollPane scrollPane = new JScrollPane(seatsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(280, 350));
        busBody.add(scrollPane, BorderLayout.CENTER);

        add(busBody, BorderLayout.CENTER);
    }

    private JToggleButton createSeatButton(int seatNum) {
        JToggleButton btn = new JToggleButton(String.valueOf(seatNum));
        btn.setPreferredSize(new Dimension(50, 35));
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        if (bookedSeats.contains(seatNum)) {
            btn.setBackground(new Color(244, 67, 54));
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
            btn.setToolTipText("Seat " + seatNum + " - Booked");
        } else {
            btn.setBackground(new Color(76, 175, 80));
            btn.setForeground(Color.WHITE);
            btn.setToolTipText("Seat " + seatNum + " - Available");
            final int seat = seatNum;
            btn.addActionListener(e -> {
                // Deselect previous
                if (selectedSeat > 0 && seatButtons.containsKey(selectedSeat)) {
                    JToggleButton prev = seatButtons.get(selectedSeat);
                    prev.setSelected(false);
                    prev.setBackground(new Color(76, 175, 80));
                }
                if (btn.isSelected()) {
                    btn.setBackground(new Color(33, 150, 243));
                    selectedSeat = seat;
                } else {
                    btn.setBackground(new Color(76, 175, 80));
                    selectedSeat = -1;
                }
            });
        }
        return btn;
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        JLabel colorBox = new JLabel("■");
        colorBox.setForeground(color);
        colorBox.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(colorBox);
        panel.add(new JLabel(text));
        return panel;
    }

    public int getSelectedSeat() {
        return selectedSeat;
    }
}
