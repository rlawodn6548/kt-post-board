package com.kt.security.wrapper;

public class InputValidator {

    private static final String SAFE_STRING_PATTERN = "^[a-zA-Z0-9\\s\\.\\?\\!\\-\\_가-힣]*$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    public static void validateUsername(String username) {
        if (username != null) {
            if (username.length() > 50) throw new IllegalArgumentException("Username exceeds maximum length of 50");
            if (!username.matches(SAFE_STRING_PATTERN)) throw new IllegalArgumentException("Invalid username format");
        }
    }

    public static void validateEmail(String email) {
        if (email != null) {
            if (email.length() > 100) throw new IllegalArgumentException("Email exceeds maximum length of 100");
            if (!email.matches(EMAIL_PATTERN)) throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static void validatePassword(String password) {
        if (password != null) {
            if (password.length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters long.");
            }
        }
    }

    public static void validateArticle(String title, String content) {
        if (title != null) {
            if (title.length() > 200) throw new IllegalArgumentException("Title exceeds maximum length of 200");
            if (!title.matches(SAFE_STRING_PATTERN)) throw new IllegalArgumentException("Invalid title format");
        }
        if (content != null) {
            if (content.length() > 4000) throw new IllegalArgumentException("Content exceeds maximum length of 4000");
        }
    }
}