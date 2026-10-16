package com.hotel.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean check(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        // Hash BCrypt bat dau bang $2a$ / $2b$ / $2y$
        if (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plainPassword, storedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        // Du lieu mau trong SQL dang luu plaintext "123456"
        return plainPassword.equals(storedPassword);
    }

    // Thêm phương thức checkPassword để tương thích với servlet
    public static boolean checkPassword(String plainPassword, String storedPassword) {
        return check(plainPassword, storedPassword);
    }
}
