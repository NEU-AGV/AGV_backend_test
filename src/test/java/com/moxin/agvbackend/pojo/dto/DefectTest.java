package com.moxin.agvbackend.pojo.entity;

import com.moxin.agvbackend.pojo.entity.Defect;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefectTest {

    @Test
    public void testSettersAndGetters() {
        Defect defect = new Defect();
        defect.setDefectId("DEF-001");
        defect.setTaskId("TASK-001");
        defect.setDefectType("裂缝");
        defect.setDiscoveryTime(LocalDateTime.now());
        assertEquals("DEF-001", defect.getDefectId());
        assertEquals("TASK-001", defect.getTaskId());
        assertEquals("裂缝", defect.getDefectType());
        assertEquals(LocalDateTime.now().getYear(), defect.getDiscoveryTime().getYear());
    }
}