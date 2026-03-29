package com.busbooking.ui;

import com.busbooking.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application frame with CardLayout for panel switching.
 * Background images: place b.jpg, u.jpg, s.jpg, i.jpg in src/main/resources/images/
 * Each panel uses a different background image.
 */
public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private User currentUser;

    // Background image paths mapped to panels
    private static final String[] BG_IMAGES = {"b.jpg", "u.jpg", "s.jpg", "i.jpg"};

    public MainFrame() {
        setTitle("🚌 Kenya Bus Booking System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(420, 750); // Slightly larger than phone, extendable
        setMinimumSize(new Dimension(380, 650));
        setLocationRelativeTo(null);
        setResizable(true);

        // Confirm on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "Are you sure you want to exit the Bus Booking System?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels with background images
        mainPanel.add(wrapWithBackground(new LoginPanel(this), "b.jpg"), "login");
        mainPanel.add(wrapWithBackground(new RegisterPanel(this), "u.jpg"), "register");
        mainPanel.add(wrapWithBackground(new AdminLoginPanel(this), "s.jpg"), "adminLogin");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    private JPanel wrapWithBackground(JPanel content, String imageName) {
        return new JPanel(new BorderLayout()) {
            private Image bgImage;

            {
                setOpaque(true);
                try {
                    java.net.URL url = getClass().getClassLoader().getResource("images/" + imageName);
                    if (url != null) {
                        bgImage = new ImageIcon(url).getImage();
                    }
                } catch (Exception e) {
                    // No background image found, use gradient fallback
                }
                add(content, BorderLayout.CENTER);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    // Semi-transparent overlay for readability
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Gradient fallback: Kenya flag colors
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0, 100, 0),
                            0, getHeight(), new Color(50, 50, 50));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
    }

    public void showPanel(String name) {
        // Dynamically create panels that need current user context
        if (name.equals("booking")) {
            // Remove old booking panel if exists
            for (Component c : mainPanel.getComponents()) {
                if ("bookingPanel".equals(c.getName())) {
                    mainPanel.remove(c);
                    break;
                }
            }
            JPanel bookingWrapped = wrapWithBackground(new BookingPanel(this), "i.jpg");
            bookingWrapped.setName("bookingPanel");
            mainPanel.add(bookingWrapped, "booking");
        } else if (name.equals("admin")) {
            for (Component c : mainPanel.getComponents()) {
                if ("adminPanel".equals(c.getName())) {
                    mainPanel.remove(c);
                    break;
                }
            }
            JPanel adminWrapped = wrapWithBackground(new AdminPanel(this), "s.jpg");
            adminWrapped.setName("adminPanel");
            mainPanel.add(adminWrapped, "admin");
        }
        cardLayout.show(mainPanel, name);
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
}
