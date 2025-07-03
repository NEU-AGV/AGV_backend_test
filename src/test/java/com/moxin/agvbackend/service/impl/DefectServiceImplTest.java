package com.moxin.agvbackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxin.agvbackend.mapper.DefectMapper;
import com.moxin.agvbackend.pojo.dto.DefectQuery;
import com.moxin.agvbackend.pojo.entity.Defect;
import com.moxin.agvbackend.pojo.vo.DefectVO;
import com.moxin.agvbackend.service.TaskService;
import com.moxin.agvbackend.service.impl.DefectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefectServiceImplTest {

    @Mock
    private DefectMapper defectMapper;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private DefectServiceImpl defectService;

    private DefectQuery query;
    private Defect defect;

    @BeforeEach
    public void setUp() {
        query = new DefectQuery();
        query.setPage(1);
        query.setPageSize(10);

        defect = new Defect();
        defect.setDefectId("123");
        defect.setCurrentStatus("NEW");
    }

    @Test
    public void testList_NormalCase() {
        // 模拟分页对象
        IPage<DefectVO> expectedPage = new Page<>();
        when(defectMapper.selectDefectWithTaskPage(any(Page.class), any(DefectQuery.class))).thenReturn(expectedPage);

        IPage<DefectVO> result = defectService.list(query);

        assertNotNull(result);
        assertEquals(expectedPage, result);
        verify(defectMapper, times(1)).selectDefectWithTaskPage(any(Page.class), any(DefectQuery.class));
    }

    @Test
    public void testSave_NormalCase() {
        defectService.save(defect);

        assertNotNull(defect.getCreatedAt());
        assertNotNull(defect.getUpdatedAt());
        verify(defectMapper, times(1)).insert(defect);
    }

    @Test
    public void testUpdateStatusByDefectId_NormalCase() {
        String defectId = "123";
        String newStatus = "IN_PROGRESS";
        when(defectMapper.selectOne(any())).thenReturn(defect);

        defectService.updateStatusByDefectId(defectId, newStatus);

        assertEquals(newStatus, defect.getCurrentStatus());
        assertNotNull(defect.getUpdatedAt());
        verify(defectMapper, times(1)).updateById(defect);
    }

    @Test
    public void testUpdateStatusByDefectId_DefectNotFound() {
        String defectId = "456";
        String newStatus = "IN_PROGRESS";
        when(defectMapper.selectOne(any())).thenReturn(null);

        defectService.updateStatusByDefectId(defectId, newStatus);

        verify(defectMapper, never()).updateById(any(Defect.class));
    }

    @Test
    public void testList_BoundaryCase_PageZero() {
        query.setPage(0);
        query.setPageSize(10);

        IPage<DefectVO> expectedPage = new Page<>();
        when(defectMapper.selectDefectWithTaskPage(any(Page.class), any(DefectQuery.class))).thenReturn(expectedPage);

        IPage<DefectVO> result = defectService.list(query);

        assertNotNull(result);
        assertEquals(expectedPage, result);
        verify(defectMapper, times(1)).selectDefectWithTaskPage(any(Page.class), any(DefectQuery.class));
    }

    @Test
    public void testList_BoundaryCase_PageSizeZero() {
        query.setPage(1);
        query.setPageSize(0);

        IPage<DefectVO> expectedPage = new Page<>();
        when(defectMapper.selectDefectWithTaskPage(any(Page.class), any(DefectQuery.class))).thenReturn(expectedPage);

        IPage<DefectVO> result = defectService.list(query);

        assertNotNull(result);
        assertEquals(expectedPage, result);
        verify(defectMapper, times(1)).selectDefectWithTaskPage(any(Page.class), any(DefectQuery.class));
    }
}