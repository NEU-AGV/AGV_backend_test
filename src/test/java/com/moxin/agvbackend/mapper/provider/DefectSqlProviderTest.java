package com.moxin.agvbackend.mapper.provider;

import com.moxin.agvbackend.pojo.dto.DefectQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefectSqlProviderTest {

    @Test
    void testBuildSelectWithTask() {
        DefectSqlProvider provider = new DefectSqlProvider();

        // 构建一个包含所有可能条件的 DefectQuery 对象
        DefectQuery query = DefectQuery.builder()
                .defectType("裂缝")
                .severity("高")
                .currentStatus("待处理")
                .discoveryStart("2024-01-01")
                .discoveryEnd("2024-12-31")
                .build();

        // 调用 buildSelectWithTask 方法生成 SQL
        String sql = provider.buildSelectWithTask(query);

        // 验证生成的 SQL 包含基本的查询结构和所有条件
        assertTrue(sql.contains("SELECT d.id, d.defect_id, d.task_id, d.defect_type, d.description, "));
        assertTrue(sql.contains("FROM defect d "));
        assertTrue(sql.contains("LEFT JOIN task t ON d.task_id = t.task_id "));
        assertTrue(sql.contains("WHERE 1 = 1 "));
        assertTrue(sql.contains("AND d.defect_type LIKE CONCAT('%', #{query.defectType}, '%') "));
        assertTrue(sql.contains("AND d.severity = #{query.severity} "));
        assertTrue(sql.contains("AND d.current_status = #{query.currentStatus} "));
        assertTrue(sql.contains("AND d.discovery_time >= #{query.discoveryStart} "));
        assertTrue(sql.contains("AND d.discovery_time <= #{query.discoveryEnd} "));

        // 构建一个不包含任何条件的 DefectQuery 对象
        DefectQuery emptyQuery = DefectQuery.builder().build();

        // 调用 buildSelectWithTask 方法生成 SQL
        String emptySql = provider.buildSelectWithTask(emptyQuery);

        // 验证生成的 SQL 只包含基本的查询结构，不包含任何条件
        assertTrue(emptySql.contains("SELECT d.id, d.defect_id, d.task_id, d.defect_type, d.description, "));
        assertTrue(emptySql.contains("FROM defect d "));
        assertTrue(emptySql.contains("LEFT JOIN task t ON d.task_id = t.task_id "));
        assertTrue(emptySql.contains("WHERE 1 = 1 "));
        assertFalse(emptySql.contains("AND d.defect_type LIKE CONCAT('%', #{query.defectType}, '%') "));
        assertFalse(emptySql.contains("AND d.severity = #{query.severity} "));
        assertFalse(emptySql.contains("AND d.current_status = #{query.currentStatus} "));
        assertFalse(emptySql.contains("AND d.discovery_time >= #{query.discoveryStart} "));
        assertFalse(emptySql.contains("AND d.discovery_time <= #{query.discoveryEnd} "));
    }
}