package com.moxin.agvbackend.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.DepartmentMapper;
import com.moxin.agvbackend.mapper.UserMapper;
import com.moxin.agvbackend.pojo.entity.Department;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.pojo.vo.DepartmentVO;
import com.moxin.agvbackend.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DepartmentServiceImplTest {

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDepartmentTree() {
        // 准备数据
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setParentId(0L);
        dept1.setDeptName("Dept1");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setParentId(1L);
        dept2.setDeptName("Dept2");

        List<Department> allDepartments = Arrays.asList(dept1, dept2);

        // 模拟数据库查询结果
        when(departmentMapper.selectList(null)).thenReturn(allDepartments);

        // 调用方法
        List<DepartmentVO> result = departmentService.getDepartmentTree();

        // 验证结果
        assertEquals(1, result.size());
        assertEquals("Dept1", result.get(0).getDeptName());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("Dept2", result.get(0).getChildren().get(0).getDeptName());

        // 验证方法调用
        verify(departmentMapper, times(1)).selectList(null);
    }

    @Test
    void testGetDepartmentTree_EmptyList() {
        // 模拟数据库查询结果为空
        when(departmentMapper.selectList(null)).thenReturn(new ArrayList<>());

        // 调用方法
        List<DepartmentVO> result = departmentService.getDepartmentTree();

        // 验证结果
        assertEquals(0, result.size());

        // 验证方法调用
        verify(departmentMapper, times(1)).selectList(null);
    }

    @Test
    void testGetDepartmentTree_ParentNotFound() {
        // 准备数据，子部门的父部门 ID 不存在于查询结果中
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setParentId(0L);
        dept1.setDeptName("Dept1");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setParentId(3L); // 父部门 ID 3 不存在于结果中
        dept2.setDeptName("Dept2");

        List<Department> allDepartments = Arrays.asList(dept1, dept2);

        // 模拟数据库查询结果
        when(departmentMapper.selectList(null)).thenReturn(allDepartments);

        // 调用方法
        List<DepartmentVO> result = departmentService.getDepartmentTree();

        // 验证结果，只有根部门会被添加到结果中
        assertEquals(1, result.size());
        assertEquals("Dept1", result.get(0).getDeptName());

        // 验证方法调用
        verify(departmentMapper, times(1)).selectList(null);
    }

    @Test
    void testCreateDepartment() {
        // 准备数据
        Department dept = new Department();
        dept.setDeptName("New Dept");

        // 调用方法
        departmentService.createDepartment(dept);

        // 验证方法调用
        verify(departmentMapper, times(1)).insert(dept);
    }

    @Test
    void testUpdateDepartment() {
        // 准备数据
        Department dept = new Department();
        dept.setId(1L);
        dept.setDeptName("Updated Dept");

        // 调用方法
        departmentService.updateDepartment(dept);

        // 验证方法调用
        verify(departmentMapper, times(1)).updateById(dept);
    }

    @Test
    void testDeleteDepartment_NoChildAndNoUser() {
        // 准备数据
        Long id = 1L;

        // 模拟数据库查询结果
        when(departmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // 调用方法
        departmentService.deleteDepartment(id);

        // 验证方法调用
        verify(departmentMapper, times(1)).deleteById(id);
    }

    @Test
    void testDeleteDepartment_HasChild() {
        // 准备数据
        Long id = 1L;

        // 模拟数据库查询结果
        when(departmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // 调用方法并验证异常
        assertThrows(AppException.class, () -> {
            departmentService.deleteDepartment(id);
        }, "请先删除子部门");

        // 验证方法调用
        verify(departmentMapper, never()).deleteById(id);
    }

    @Test
    void testDeleteDepartment_HasUser() {
        // 准备数据
        Long id = 1L;

        // 模拟数据库查询结果
        when(departmentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // 调用方法并验证异常
        assertThrows(AppException.class, () -> {
            departmentService.deleteDepartment(id);
        }, "该部门下仍有用户，无法删除");

        // 验证方法调用
        verify(departmentMapper, never()).deleteById(id);
    }

    @Test
    void testDeleteDepartment_NullId() {
        // 调用方法并验证异常
        assertThrows(NullPointerException.class, () -> {
            departmentService.deleteDepartment(null);
        }, "传入的部门 ID 不能为 null");

        // 验证方法调用
        verify(departmentMapper, never()).deleteById(anyLong());
    }
}