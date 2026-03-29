package com.busbooking.ui;

import com.busbooking.db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminLoginPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final MainFrame mainFrame;

    public AdminLoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        card.setPreferredSize(new Dimension(340, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel title = new JLabel("🛡️ Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(187, 0, 0));
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        card.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        card.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        card.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        card.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("LOGIN AS ADMIN");
        loginBtn.setBackground(new Color(187, 0, 0));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> handleAdminLogin());
        card.add(loginBtn, gbc);

        gbc.gridy = 4;
        JButton backBtn = new JButton("← Back to User Login");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(33, 150, 243));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.showPanel("login"));
        card.add(backBtn, gbc);

        add(card);
    }

    private void handleAdminLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM admins WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                mainFrame.showPanel("admin");
                JOptionPane.showMessageDialog(this, "Welcome, Admin!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
