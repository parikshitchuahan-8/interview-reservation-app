package com.example.system.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte hashByte : hashBytes) {
                hash.append(String.format("%02x", hashByte));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        return storedPassword != null
                && (storedPassword.equals(rawPassword) || storedPassword.equals(hash(rawPassword)));
    }
}
