package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.entity.Department;
import com.moxin.agvbackend.pojo.vo.DepartmentVO;
import com.moxin.agvbackend.service.DepartmentService;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.utils.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /** 4.1 获取部门树 */
    @GetMapping
    public Result getDeptTree() {
        List<DepartmentVO> tree = departmentService.getDepartmentTree();
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("获取成功")
                .data(tree)
                .build();
    }

    /** 4.2 新增部门 */
    @PostMapping
    public Result create(@RequestBody Department department) {
        departmentService.createDepartment(department);
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("新增成功")
                .build();
    }

    /** 4.2 修改部门 */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("修改成功")
                .build();
    }

    /** 4.3 删除部门 */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("删除成功")
                .build();
    }
}
