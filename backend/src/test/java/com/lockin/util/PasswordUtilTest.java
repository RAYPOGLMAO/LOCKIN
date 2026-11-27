package com.lockin.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashCanBeVerified() {
        String hash = PasswordUtil.hash("correct horse battery staple");
        assertTrue(PasswordUtil.verify("correct horse battery staple", hash));
        assertFalse(PasswordUtil.verify("wrong password", hash));
    }

    @Test
    void hashesAreSalted() {
        assertNotEquals(PasswordUtil.hash("same password"), PasswordUtil.hash("same password"));
    }

    @Test
    void malformedHashFailsVerification() {
        assertFalse(PasswordUtil.verify("anything", "not-a-valid-hash"));
    }
}
