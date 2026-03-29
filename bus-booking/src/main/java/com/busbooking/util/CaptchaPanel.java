package com.busbooking.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaPanel extends JPanel {
    private String captchaText;
    private final JTextField inputField;
    private final Random random = new Random();

    public CaptchaPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setOpaque(false);
        generateCaptcha();
        JLabel captchaImage = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCaptcha(g);
            }
        };
        captchaImage.setPreferredSize(new Dimension(150, 40));
        captchaImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        inputField = new JTextField(8);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton refreshBtn = new JButton("↻");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 16));
        refreshBtn.setForeground(new Color(0, 128, 0));
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> {
            generateCaptcha();
            captchaImage.repaint();
            inputField.setText("");
        });

        add(new JLabel("Captcha:"));
        add(captchaImage);
        add(inputField);
        add(refreshBtn);
    }

    private void generateCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        captchaText = sb.toString();
    }

    private void drawCaptcha(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(245, 245, 220));
        g2.fillRect(0, 0, 150, 40);

        // Noise lines
        for (int i = 0; i < 5; i++) {
            g2.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g2.drawLine(random.nextInt(150), random.nextInt(40), random.nextInt(150), random.nextInt(40));
        }

        g2.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
        for (int i = 0; i < captchaText.length(); i++) {
            g2.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            int x = 10 + i * 22;
            int y = 25 + random.nextInt(10) - 5;
            double angle = (random.nextDouble() - 0.5) * 0.4;
            g2.rotate(angle, x, y);
            g2.drawString(String.valueOf(captchaText.charAt(i)), x, y);
            g2.rotate(-angle, x, y);
        }
    }

    public boolean verify() {
        return inputField.getText().equals(captchaText);
    }

    public void reset() {
        generateCaptcha();
        inputField.setText("");
        repaint();
    }
}
