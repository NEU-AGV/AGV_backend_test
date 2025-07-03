package com.moxin.agvbackend.service;


import com.moxin.agvbackend.pojo.entity.Department;
import com.moxin.agvbackend.pojo.vo.DepartmentVO;

import java.util.List;

public interface DepartmentService {
    List<DepartmentVO> getDepartmentTree();
    void createDepartment(Department dept);
    void updateDepartment(Department dept);
    void deleteDepartment(Long id);
}
