package com.moxin.agvbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.TaskMapper;
import com.moxin.agvbackend.pojo.dto.TaskQueryDTO;
import com.moxin.agvbackend.pojo.entity.Task;
import com.moxin.agvbackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() throws Exception {
        // 通过反射设置baseMapper解决MyBatis-Plus的初始化问题
        Field baseMapperField = taskService.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(taskService, taskMapper);
    }

    private Task createTestTask(String taskId, String taskName, String status, LocalDateTime plannedTime) {
        return Task.builder()
                .taskId(taskId)
                .taskName(taskName)
                .status(status)
                .plannedStartTime(plannedTime)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getTaskPage_WithFullFilters_ShouldReturnFilteredResults() {
        // 准备测试数据
        String month = "2025-07";
        TaskQueryDTO dto = TaskQueryDTO.builder()
                .taskName("巡检")
                .status("进行中")
                .month(month)
                .build();

        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime plannedTime = startDate.plusDays(1).atStartOfDay();

        List<Task> mockTasks = Collections.singletonList(
                createTestTask("TASK-001", "日常巡检", "进行中", plannedTime)
        );

        Page<Task> mockPage = new Page<>(1, 10, 1);
        mockPage.setRecords(mockTasks);

        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        IPage<Task> result = taskService.getTaskPage(1, 10, dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.getTotal(), "总记录数不正确");
        assertEquals("日常巡检", result.getRecords().get(0).getTaskName(), "任务名称不匹配");
    }

    @Test
    void exportTasks_ShouldConfigureResponseCorrectly() throws Exception {
        // 准备测试数据
        List<Task> mockTasks = Arrays.asList(
                createTestTask("TASK-001", "任务1", "已完成", LocalDateTime.now()),
                createTestTask("TASK-002", "任务2", "进行中", LocalDateTime.now())
        );
        when(taskMapper.selectList(any())).thenReturn(mockTasks);

        // 使用Mock输出流
        when(response.getOutputStream()).thenReturn(new MockServletOutputStream());

        // 执行测试
        taskService.exportTasks(new HashMap<>(), response);

        // 验证结果
        verify(response).setContentType("application/vnd.ms-excel");
        verify(response).setHeader("Content-Disposition", "attachment; filename=tasks.xlsx");
    }

    @Test
    void exportTasks_WhenIOException_ShouldThrowAppException() throws Exception {
        // 准备测试数据
        when(taskMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(response.getOutputStream()).thenThrow(new IOException("模拟IO异常"));

        // 执行并验证异常
        AppException exception = assertThrows(AppException.class,
                () -> taskService.exportTasks(new HashMap<>(), response),
                "应抛出AppException");

        assertEquals(ResultCode.FAIL, exception.getCode(), "错误代码不匹配");
        assertEquals("导出失败", exception.getMessage(), "错误消息不匹配");
    }

    // 模拟ServletOutputStream
    private static class MockServletOutputStream extends jakarta.servlet.ServletOutputStream {
        @Override
        public void write(int b) {
            // 模拟输出
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
        }
    }

    @Test
    void getTaskPage_WithoutFilters_ShouldReturnAllResults() {
        // 准备测试数据
        TaskQueryDTO dto = new TaskQueryDTO();
        List<Task> mockTasks = Arrays.asList(
                createTestTask("TASK-001", "任务1", "已完成", LocalDateTime.now()),
                createTestTask("TASK-002", "任务2", "进行中", LocalDateTime.now())
        );

        Page<Task> mockPage = new Page<>(1, 10, 2);
        mockPage.setRecords(mockTasks);

        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        IPage<Task> result = taskService.getTaskPage(1, 10, dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(2, result.getTotal(), "总记录数不正确");
        assertEquals(2, result.getRecords().size(), "记录数不正确");
    }

    @Test
    void getTaskPage_WithTaskNameFilter_ShouldReturnFilteredResults() {
        // 准备测试数据
        TaskQueryDTO dto = TaskQueryDTO.builder()
                .taskName("巡检")
                .build();

        List<Task> mockTasks = Collections.singletonList(
                createTestTask("TASK-001", "日常巡检", "已完成", LocalDateTime.now())
        );

        Page<Task> mockPage = new Page<>(1, 10, 1);
        mockPage.setRecords(mockTasks);

        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        IPage<Task> result = taskService.getTaskPage(1, 10, dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.getTotal(), "总记录数不正确");
        assertEquals("日常巡检", result.getRecords().get(0).getTaskName(), "任务名称不匹配");
    }

    @Test
    void getTaskPage_WithStatusFilter_ShouldReturnFilteredResults() {
        // 准备测试数据
        TaskQueryDTO dto = TaskQueryDTO.builder()
                .status("已完成")
                .build();

        List<Task> mockTasks = Collections.singletonList(
                createTestTask("TASK-001", "任务1", "已完成", LocalDateTime.now())
        );

        Page<Task> mockPage = new Page<>(1, 10, 1);
        mockPage.setRecords(mockTasks);

        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        IPage<Task> result = taskService.getTaskPage(1, 10, dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.getTotal(), "总记录数不正确");
        assertEquals("已完成", result.getRecords().get(0).getStatus(), "状态不匹配");
    }

    @Test
    void getTaskPage_WithMonthFilter_ShouldReturnFilteredResults() {
        // 准备测试数据
        String month = "2025-07";
        TaskQueryDTO dto = TaskQueryDTO.builder()
                .month(month)
                .build();

        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDateTime plannedTime = startDate.plusDays(1).atStartOfDay();

        List<Task> mockTasks = Collections.singletonList(
                createTestTask("TASK-001", "任务1", "已完成", plannedTime)
        );

        Page<Task> mockPage = new Page<>(1, 10, 1);
        mockPage.setRecords(mockTasks);

        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        // 执行测试
        IPage<Task> result = taskService.getTaskPage(1, 10, dto);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(1, result.getTotal(), "总记录数不正确");
        assertEquals(plannedTime, result.getRecords().get(0).getPlannedStartTime(), "计划时间不匹配");
    }

    @Test
    void findNameMapByIds_WithEmptySet_ShouldReturnEmptyMap() {
        // 执行测试
        Map<String, String> result = taskService.findNameMapByIds(Collections.emptySet());

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.isEmpty(), "返回的映射应为空");
    }

    @Test
    void findNameMapByIds_WithValidIds_ShouldReturnNameMap() {
        // 准备测试数据
        Set<String> taskIds = new HashSet<>(Arrays.asList("TASK-001", "TASK-002"));
        List<Task> mockTasks = Arrays.asList(
                createTestTask("TASK-001", "任务1", "已完成", LocalDateTime.now()),
                createTestTask("TASK-002", "任务2", "进行中", LocalDateTime.now())
        );

        when(taskMapper.selectList(any())).thenReturn(mockTasks);

        // 执行测试
        Map<String, String> result = taskService.findNameMapByIds(taskIds);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertEquals(2, result.size(), "映射大小不正确");
        assertEquals("任务1", result.get("TASK-001"), "任务1名称不匹配");
        assertEquals("任务2", result.get("TASK-002"), "任务2名称不匹配");
    }

    @Test
    void exportTasks_WithEmptyTaskList_ShouldExportEmptyFile() throws Exception {
        // 准备测试数据
        when(taskMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(response.getOutputStream()).thenReturn(new MockServletOutputStream());

        // 执行测试
        taskService.exportTasks(new HashMap<>(), response);

        // 验证结果
        verify(response).setContentType("application/vnd.ms-excel");
        verify(response).setHeader("Content-Disposition", "attachment; filename=tasks.xlsx");
        verify(taskMapper).selectList(any());
    }
    @Test
    void findNameMapByIds_WithNullSet_ShouldReturnEmptyMap() {
        // 执行测试
        Map<String, String> result = taskService.findNameMapByIds(null);

        // 验证结果
        assertNotNull(result, "返回结果不应为null");
        assertTrue(result.isEmpty(), "返回的映射应为空");
    }

}