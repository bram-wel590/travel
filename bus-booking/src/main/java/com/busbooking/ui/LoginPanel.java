package com.busbooking.ui;

import com.busbooking.db.DatabaseConnection;
import com.busbooking.model.User;
import com.busbooking.util.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPanel extends JPanel {
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 100, 0), 2, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        card.setPreferredSize(new Dimension(350, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel title = new JLabel("🚌 Bus Booking Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 100, 0));
        card.add(title, gbc);

        gbc.gridwidth = 1;

        // Email
        gbc.gridy = 1; gbc.gridx = 0;
        card.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        card.add(emailField, gbc);

        // Password
        gbc.gridy = 2; gbc.gridx = 0;
        card.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        card.add(passwordField, gbc);

        // Login button
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(0, 100, 0));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn, gbc);

        // Register link
        gbc.gridy = 4;
        JButton registerBtn = new JButton("Don't have an account? Register");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(new Color(33, 150, 243));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> mainFrame.showPanel("register"));
        card.add(registerBtn, gbc);

        // Admin login link
        gbc.gridy = 5;
        JButton adminBtn = new JButton("Admin Login →");
        adminBtn.setBorderPainted(false);
        adminBtn.setContentAreaFilled(false);
        adminBtn.setForeground(new Color(187, 0, 0));
        adminBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adminBtn.addActionListener(e -> mainFrame.showPanel("adminLogin"));
        card.add(adminBtn, gbc);

        add(card);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!InputValidator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!InputValidator.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("phone"));
                mainFrame.setCurrentUser(user);
                mainFrame.showPanel("booking");
                JOptionPane.showMessageDialog(this, "Welcome back, " + user.getFullName() + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
