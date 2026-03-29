package com.busbooking;

import com.busbooking.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Kenya Bus Booking System - Main Entry Point
 * 
 * Prerequisites:
 * 1. Install MySQL and create the database using sql/schema.sql
 * 2. Update DatabaseConnection.java with your MySQL credentials
 * 3. Place background images (b.jpg, u.jpg, s.jpg, i.jpg) in src/main/resources/images/
 * 4. For M-Pesa: Update MpesaService.java with your Daraja API credentials from https://developer.safaricom.co.ke
 * 
 * To run:
 *   mvn clean compile exec:java -Dexec.mainClass="com.busbooking.BusBookingApp"
 * 
 * To package:
 *   mvn clean package
 *   java -jar target/kenya-bus-booking-1.0-SNAPSHOT.jar
 */
public class BusBookingApp {
    public static void main(String[] args) {
        // Set modern look and feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 5);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
