package com.busbooking.ui;

import com.busbooking.db.DatabaseConnection;
import com.busbooking.util.CaptchaPanel;
import com.busbooking.util.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPanel extends JPanel {
    private final JTextField nameField, emailField, phoneField;
    private final JPasswordField passwordField, confirmField;
    private final CaptchaPanel captchaPanel;
    private final MainFrame mainFrame;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("📝 Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(187, 0, 0));
        card.add(title, gbc);

        gbc.gridwidth = 1;

        // Name
        gbc.gridy = 1; gbc.gridx = 0;
        card.add(new JLabel("Full Name (max 20, no numbers):"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        card.add(nameField, gbc);

        // Email
        gbc.gridy = 2; gbc.gridx = 0;
        card.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        card.add(emailField, gbc);

        // Phone
        gbc.gridy = 3; gbc.gridx = 0;
        card.add(new JLabel("Phone (10 digits only):"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        card.add(phoneField, gbc);

        // Password
        gbc.gridy = 4; gbc.gridx = 0;
        card.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        card.add(passwordField, gbc);

        // Confirm
        gbc.gridy = 5; gbc.gridx = 0;
        card.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmField = new JPasswordField(15);
        card.add(confirmField, gbc);

        // Captcha
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        captchaPanel = new CaptchaPanel();
        card.add(captchaPanel, gbc);

        // Register button
        gbc.gridy = 7;
        JButton regBtn = new JButton("REGISTER");
        regBtn.setBackground(new Color(187, 0, 0));
        regBtn.setForeground(Color.WHITE);
        regBtn.setFont(new Font("Arial", Font.BOLD, 14));
        regBtn.setFocusPainted(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> handleRegister());
        card.add(regBtn, gbc);

        // Back to login
        gbc.gridy = 8;
        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(33, 150, 243));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.showPanel("login"));
        card.add(backBtn, gbc);

        add(card);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (!InputValidator.isValidName(name)) {
            JOptionPane.showMessageDialog(this, "Name must be letters only, max 20 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!InputValidator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!InputValidator.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Phone must be exactly 10 digits, no letters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!InputValidator.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!captchaPanel.verify()) {
            JOptionPane.showMessageDialog(this, "CAPTCHA verification failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            captchaPanel.reset();
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (full_name, email, phone, password) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, password);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Account created successfully! Please login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showPanel("login");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
