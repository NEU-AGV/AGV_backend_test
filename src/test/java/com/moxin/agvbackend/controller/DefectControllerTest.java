package com.moxin.agvbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxin.agvbackend.pojo.dto.DefectQuery;
import com.moxin.agvbackend.pojo.dto.StatusDTO;
import com.moxin.agvbackend.pojo.entity.Defect;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.vo.DefectVO;
import com.moxin.agvbackend.service.DefectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefectControllerTest {

    @Mock
    private DefectService defectService;

    @InjectMocks
    private DefectController defectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnResultWithDefectList() {
        // Arrange
        DefectQuery query = new DefectQuery();
        Page<DefectVO> mockPage = new Page<>();
        mockPage.setRecords(Collections.singletonList(createMockDefectVO()));
        mockPage.setTotal(1L);

        when(defectService.list(any(DefectQuery.class))).thenReturn(mockPage);

        // Act
        Result result = defectController.list(query);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("获取成功", result.getMessage());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(1L, data.get("total"));
        assertFalse(((java.util.List<?>) data.get("list")).isEmpty());
    }

    @Test
    void create_ShouldReturnSuccessResult() {
        // Arrange
        Defect defect = new Defect();
        defect.setDefectId("DEF-001");

        doNothing().when(defectService).save(any(Defect.class));

        // Act
        Result result = defectController.create(defect);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("新增成功", result.getMessage());
        verify(defectService, times(1)).save(defect);
    }

    @Test
    void updateStatus_ShouldReturnSuccessResult() {
        // Arrange
        String defectId = "DEF-001";
        StatusDTO dto = new StatusDTO();
        dto.setStatus("RESOLVED");

        doNothing().when(defectService).updateStatusByDefectId(anyString(), anyString());

        // Act
        Result result = defectController.updateStatus(defectId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("状态更新成功", result.getMessage());
        verify(defectService, times(1)).updateStatusByDefectId(defectId, "RESOLVED");
    }

    @Test
    void updateStatus_WithEmptyDefectId_ShouldStillCallService() {
        // Arrange
        String defectId = "";
        StatusDTO dto = new StatusDTO();
        dto.setStatus("RESOLVED");

        doNothing().when(defectService).updateStatusByDefectId(anyString(), anyString());

        // Act
        Result result = defectController.updateStatus(defectId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(defectService, times(1)).updateStatusByDefectId("", "RESOLVED");
    }

    @Test
    void updateStatus_WithNullStatus_ShouldStillCallService() {
        // Arrange
        String defectId = "DEF-001";
        StatusDTO dto = new StatusDTO();
        dto.setStatus(null);

        doNothing().when(defectService).updateStatusByDefectId(anyString(), anyString());

        // Act
        Result result = defectController.updateStatus(defectId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(defectService, times(1)).updateStatusByDefectId("DEF-001", null);
    }

    private DefectVO createMockDefectVO() {
        DefectVO defectVO = new DefectVO();
        defectVO.setId(1L);
        defectVO.setDefectId("DEF-001");
        defectVO.setTaskId("TASK-001");
        defectVO.setTaskName("Test Task");
        defectVO.setDefectType("Type A");
        defectVO.setDescription("Test defect");
        defectVO.setLocation("Location A");
        defectVO.setImageUrl("https://example.com/image.jpg");  // 改为HTTPS
        defectVO.setSeverity("Medium");
        defectVO.setIsVerified(false);
        defectVO.setCurrentStatus("Open");
        defectVO.setDiscoverer("Tester");
        defectVO.setDiscoveryTime(LocalDateTime.now());
        defectVO.setDiscoveryMethod("Manual");
        defectVO.setHandler("Handler");
        return defectVO;
    }
}