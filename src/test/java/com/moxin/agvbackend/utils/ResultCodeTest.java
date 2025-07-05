package com.moxin.agvbackend.utils;

import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultCodeTest {

    @Test
    public void testSuccessCode() {
        assertEquals(200, ResultCode.SUCCESS);
    }

    @Test
    public void testFailCode() {
        assertEquals(400, ResultCode.FAIL);
    }

    @Test
    public void testUnauthorizedCode() {
        assertEquals(401, ResultCode.UNAUTHORIZED);
    }
}