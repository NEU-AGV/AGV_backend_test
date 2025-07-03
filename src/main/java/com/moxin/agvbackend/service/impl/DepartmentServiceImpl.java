package com.moxin.agvbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.DepartmentMapper;
import com.moxin.agvbackend.mapper.UserMapper;
import com.moxin.agvbackend.pojo.entity.Department;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.pojo.vo.DepartmentVO;
import com.moxin.agvbackend.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;

    @Override
    public List<DepartmentVO> getDepartmentTree() {
        List<Department> all = departmentMapper.selectList(null);

        Map<Long, DepartmentVO> voMap = all.stream().map(d -> {
            DepartmentVO vo = DepartmentVO.builder()
                    .id(d.getId())
                    .parentId(d.getParentId())
                    .deptName(d.getDeptName())
                    .orderNum(d.getOrderNum())
                    .leader(d.getLeader())
                    .phone(d.getPhone())
                    .email(d.getEmail())
                    .status(d.getStatus())
                    .children(new ArrayList<>())
                    .build();
            return vo;
        }).collect(Collectors.toMap(DepartmentVO::getId, v -> v));

        List<DepartmentVO> roots = new ArrayList<>();
        for (DepartmentVO vo : voMap.values()) {
            if (vo.getParentId() == 0) {
                roots.add(vo);
            } else {
                DepartmentVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }
        return roots;
    }

    @Override
    public void createDepartment(Department dept) {
        departmentMapper.insert(dept);
    }

    @Override
    public void updateDepartment(Department dept) {
        departmentMapper.updateById(dept);
    }
    @Override
    public void deleteDepartment(Long id) {
        // 判断是否有子部门
        int childCount = Math.toIntExact(departmentMapper.selectCount(
                new LambdaQueryWrapper<Department>().eq(Department::getParentId, id)
        ));
        if (childCount > 0) {
            throw new AppException(400, "请先删除子部门");
        }

        // 判断是否有用户挂靠该部门
        int userCount = Math.toIntExact(userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getDepartmentId, id.intValue())
        ));
        if (userCount > 0) {
            throw new AppException(400, "该部门下仍有用户，无法删除");
        }

        // 执行删除
        departmentMapper.deleteById(id);
    }
}
