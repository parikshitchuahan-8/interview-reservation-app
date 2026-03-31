package com.example.system.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTests {

    @Test
    void hashesPasswordDeterministically() {
        String hash = PasswordUtil.hash("secret123");

        assertNotEquals("secret123", hash);
        assertTrue(PasswordUtil.matches("secret123", hash));
        assertFalse(PasswordUtil.matches("wrong", hash));
    }
}
