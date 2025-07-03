package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.entity.Department;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.vo.DepartmentVO;
import com.moxin.agvbackend.service.DepartmentService;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDeptTree_ShouldReturnDepartmentTree() {
        // 准备测试数据
        DepartmentVO childDept = DepartmentVO.builder()
                .id(2L)
                .parentId(1L)
                .deptName("子部门")
                .build();

        DepartmentVO rootDept = DepartmentVO.builder()
                .id(1L)
                .parentId(0L)
                .deptName("根部门")
                .children(Arrays.asList(childDept))
                .build();

        List<DepartmentVO> mockTree = Arrays.asList(rootDept);

        when(departmentService.getDepartmentTree()).thenReturn(mockTree);

        Result result = departmentController.getDeptTree();

        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("获取成功", result.getMessage());
        assertNotNull(result.getData());

        List<DepartmentVO> tree = (List<DepartmentVO>) result.getData();
        assertEquals(1, tree.size());
        assertEquals("根部门", tree.get(0).getDeptName());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("子部门", tree.get(0).getChildren().get(0).getDeptName());

        verify(departmentService, times(1)).getDepartmentTree();
    }

    @Test
    void create_ShouldCreateDepartmentSuccessfully() {
        Department department = Department.builder()
                .parentId(1L)
                .deptName("新部门")
                .orderNum(1)
                .leader("张三")
                .phone("13800138000")
                .email("zhangsan@example.com")
                .status("0")
                .build();

        Result result = departmentController.create(department);

        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("新增成功", result.getMessage());
        assertNull(result.getData());

        verify(departmentService, times(1)).createDepartment(department);
    }

    @Test
    void create_WithNullDepartment_ShouldCallServiceWithNull() {
        Result result = departmentController.create(null);

        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("新增成功", result.getMessage());

        verify(departmentService, times(1)).createDepartment(null);
    }

    @Test
    void update_ShouldUpdateDepartmentSuccessfully() {
        Long deptId = 1L;
        Department department = Department.builder()
                .deptName("更新后的部门名称")
                .orderNum(2)
                .leader("李四")
                .phone("13900139000")
                .email("lisi@example.com")
                .status("1")
                .build();

        Result result = departmentController.update(deptId, department);

        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("修改成功", result.getMessage());
        assertNull(result.getData());

        assertEquals(deptId, department.getId());
        verify(departmentService, times(1)).updateDepartment(department);
    }

    @Test
    void delete_ShouldDeleteDepartmentSuccessfully() {
        Long deptId = 1L;

        Result result = departmentController.delete(deptId);

        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("删除成功", result.getMessage());
        assertNull(result.getData());

        verify(departmentService, times(1)).deleteDepartment(deptId);
    }

    @Test
    void update_WithNullId_ShouldSetIdCorrectly() {
        Department department = new Department();
        department.setDeptName("测试部门");

        Result result = departmentController.update(5L, department);

        assertEquals(5L, department.getId());
        assertEquals(ResultCode.SUCCESS, result.getCode());
    }

    @Test
    void delete_WithZeroId_ShouldCallService() {
        Result result = departmentController.delete(0L);

        assertEquals(ResultCode.SUCCESS, result.getCode());
        verify(departmentService, times(1)).deleteDepartment(0L);
    }
}