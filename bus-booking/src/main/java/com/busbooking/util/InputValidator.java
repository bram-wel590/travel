package com.busbooking.util;

public class InputValidator {
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 20) return false;
        return name.matches("^[a-zA-Z\\s]+$");
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.length() != 10) return false;
        return phone.matches("^[0-9]{10}$");
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
