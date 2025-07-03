package com.moxin.agvbackend.pojo.dto;

import com.moxin.agvbackend.pojo.dto.LoginDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginDTOTest {

    @Test
    public void testSetters() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testUser");
        dto.setPassword("testPassword");
        dto.setCaptcha("1234");
        dto.setUuid("1234567890");
        assertEquals("testUser", dto.getUsername());
        assertEquals("testPassword", dto.getPassword());
        assertEquals("1234", dto.getCaptcha());
        assertEquals("1234567890", dto.getUuid());
    }
}
