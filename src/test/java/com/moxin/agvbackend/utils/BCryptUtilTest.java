package com.moxin.agvbackend.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class BCryptUtilTest {

    @Test
    void encryptPassword_ShouldReturnEncryptedString() {
        // Arrange
        String password = "testPassword123";

        // Act
        String result = BCryptUtil.encryptPassword(password);

        // Assert
        assertNotNull(result);
        assertTrue(result.length() > 0);
        assertNotEquals(password, result);
    }

    @Test
    void matchPassword_ShouldReturnTrueForMatchingPasswords() {
        // Arrange
        String rawPassword = "testPassword123";
        String encodedPassword = BCryptUtil.encryptPassword(rawPassword);

        // Act
        boolean result = BCryptUtil.matchPassword(rawPassword, encodedPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void matchPassword_ShouldReturnFalseForNonMatchingPasswords() {
        // Arrange
        String rawPassword = "testPassword123";
        String wrongPassword = "wrongPassword456";
        String encodedPassword = BCryptUtil.encryptPassword(rawPassword);

        // Act
        boolean result = BCryptUtil.matchPassword(wrongPassword, encodedPassword);

        // Assert
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "\t", "\n"})
    void encryptPassword_ShouldHandleBlankInputs(String password) {
        // Act
        String result = BCryptUtil.encryptPassword(password);

        // Assert
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "\t", "\n"})
    void matchPassword_ShouldHandleBlankInputs(String password) {
        // Arrange
        String encodedPassword = BCryptUtil.encryptPassword(password);

        // Act
        boolean result = BCryptUtil.matchPassword(password, encodedPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void matchPassword_ShouldThrowExceptionForNullRawPassword() {
        // Arrange
        String encodedPassword = BCryptUtil.encryptPassword("somePassword");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> BCryptUtil.matchPassword(null, encodedPassword));
    }

    @Test
    void matchPassword_ShouldReturnFalseForNullEncodedPassword() {
        // Act
        boolean result = BCryptUtil.matchPassword("somePassword", null);

        // Assert
        assertFalse(result);
    }

    @Test
    void encryptPassword_ShouldThrowExceptionForNullInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> BCryptUtil.encryptPassword(null));
    }
}