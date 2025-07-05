package com.moxin.agvbackend.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadLocalUtilTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        // 确保每个测试前后都清理ThreadLocal
        ThreadLocalUtil.remove();
    }

    @Test
    void setAndGet_ShouldWorkCorrectly() {
        // Arrange
        String testValue = "testValue123";

        // Act
        ThreadLocalUtil.set(testValue);
        String retrievedValue = ThreadLocalUtil.get();

        // Assert
        assertEquals(testValue, retrievedValue);
    }

    @Test
    void get_ShouldReturnNullWhenNotSet() {
        // Act
        Object result = ThreadLocalUtil.get();

        // Assert
        assertNull(result);
    }

    @Test
    void remove_ShouldClearValue() {
        // Arrange
        Integer testValue = 42;
        ThreadLocalUtil.set(testValue);

        // Act
        ThreadLocalUtil.remove();
        Integer result = ThreadLocalUtil.get();

        // Assert
        assertNull(result);
    }

    @Test
    void shouldWorkWithDifferentTypes() {
        // Test with String
        String stringValue = "string value";
        ThreadLocalUtil.set(stringValue);
        assertEquals(stringValue, ThreadLocalUtil.get());

        // Test with Integer
        Integer intValue = 100;
        ThreadLocalUtil.set(intValue);
        assertEquals(intValue, ThreadLocalUtil.get());

        // Test with custom object
        CustomObject customValue = new CustomObject("test");
        ThreadLocalUtil.set(customValue);
        assertEquals(customValue, ThreadLocalUtil.get());
    }

    @Test
    void shouldBeThreadSpecific() throws InterruptedException {
        // Arrange
        String mainThreadValue = "mainThread";
        ThreadLocalUtil.set(mainThreadValue);

        // Act
        Thread otherThread = new Thread(() -> {
            // Verify other thread doesn't see main thread's value
            assertNull(ThreadLocalUtil.get());

            // Set value in other thread
            String otherThreadValue = "otherThread";
            ThreadLocalUtil.set(otherThreadValue);

            // Verify other thread sees its own value
            assertEquals(otherThreadValue, ThreadLocalUtil.get());
        });

        otherThread.start();
        otherThread.join();

        // Assert
        // Main thread should still see its original value
        assertEquals(mainThreadValue, ThreadLocalUtil.get());
    }

    // Helper class for testing
    private static class CustomObject {
        private String value;

        public CustomObject(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CustomObject that = (CustomObject) obj;
            return value.equals(that.value);
        }
    }
}