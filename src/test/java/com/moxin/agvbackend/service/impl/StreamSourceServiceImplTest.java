package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.mapper.StreamSourceMapper;
import com.moxin.agvbackend.pojo.entity.StreamSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamSourceServiceImplTest {

    @Mock
    private StreamSourceMapper mapper;

    @InjectMocks
    private StreamSourceServiceImpl service;

    // ========================= listAll() 测试 =========================
    @Test
    void listAll_ShouldReturnSources_WhenSourcesExist() {
        // 模拟数据
        StreamSource source1 = new StreamSource();
        StreamSource source2 = new StreamSource();
        List<StreamSource> expectedSources = Arrays.asList(source1, source2);

        // 模拟行为
        when(mapper.selectList(null)).thenReturn(expectedSources);

        // 执行测试
        List<StreamSource> result = service.listAll();

        // 验证结果
        assertEquals(2, result.size());
        assertSame(expectedSources, result);
        verify(mapper, times(1)).selectList(null);
    }

    @Test
    void listAll_ShouldReturnEmptyList_WhenNoSourcesExist() {
        when(mapper.selectList(null)).thenReturn(Collections.emptyList());

        List<StreamSource> result = service.listAll();

        assertTrue(result.isEmpty());
        verify(mapper, times(1)).selectList(null);
    }

    // ========================= getById() 测试 =========================
    @Test
    void getById_ShouldReturnSource_WhenIdExists() {
        Long id = 1L;
        StreamSource expectedSource = new StreamSource();
        expectedSource.setId(id);

        when(mapper.selectById(id)).thenReturn(expectedSource);

        StreamSource result = service.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(mapper, times(1)).selectById(id);
    }

    @Test
    void getById_ShouldReturnNull_WhenIdNotExist() {
        Long id = 999L;

        when(mapper.selectById(id)).thenReturn(null);

        StreamSource result = service.getById(id);

        assertNull(result);
        verify(mapper, times(1)).selectById(id);
    }

    // 边界测试：最小ID值
    @Test
    void getById_ShouldReturnSource_WhenIdIsMinValue() {
        Long minId = Long.MIN_VALUE;
        StreamSource source = new StreamSource();

        when(mapper.selectById(minId)).thenReturn(source);

        StreamSource result = service.getById(minId);

        assertNotNull(result);
        verify(mapper, times(1)).selectById(minId);
    }

    // ========================= add() 测试 =========================
    @Test
    void add_ShouldSetTimestampsAndCallInsert_WhenSourceValid() {
        StreamSource source = new StreamSource();
        source.setName("Test Source");

        service.add(source);

        // 验证时间戳被设置
        assertNotNull(source.getCreatedAt());
        assertNotNull(source.getUpdatedAt());
        // 验证时间戳相等（新增时两个时间相同）
        assertEquals(source.getCreatedAt(), source.getUpdatedAt());
        // 验证插入方法被调用
        verify(mapper, times(1)).insert(source);
    }

    @Test
    void add_ShouldThrowException_WhenSourceIsNull() {
        assertThrows(NullPointerException.class, () -> service.add(null));
        verify(mapper, never()).insert((StreamSource) any());
    }

    // ========================= update() 测试 =========================
    @Test
    void update_ShouldSetUpdatedAtAndCallUpdate_WhenSourceValid() {
        StreamSource source = new StreamSource();
        source.setId(1L);
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        source.setCreatedAt(originalCreatedAt);

        service.update(source);

        // 验证更新时间被更新
        assertNotNull(source.getUpdatedAt());
        // 验证创建时间未改变
        assertEquals(originalCreatedAt, source.getCreatedAt());
        // 验证更新时间晚于创建时间
        assertTrue(source.getUpdatedAt().isAfter(originalCreatedAt));
        verify(mapper, times(1)).updateById(source);
    }

    @Test
    void update_ShouldThrowException_WhenSourceIsNull() {
        assertThrows(NullPointerException.class, () -> service.update(null));
        verify(mapper, never()).updateById((StreamSource) any());
    }

    // 修复：更新不存在的ID，模拟返回0
    @Test
    void update_ShouldNotFail_WhenIdNotExist() {
        StreamSource source = new StreamSource();
        source.setId(999L); // 不存在的ID

        // 模拟更新操作返回0（表示0条记录被更新）
        when(mapper.updateById(source)).thenReturn(0);

        service.update(source);

        verify(mapper, times(1)).updateById(source);
    }

    // ========================= delete() 测试 =========================
    @Test
    void delete_ShouldCallMapper_WhenIdValid() {
        Long id = 1L;

        service.delete(id);

        verify(mapper, times(1)).deleteById(id);
    }

    // 修复：模拟传入null时抛出IllegalArgumentException
    @Test
    void delete_ShouldThrowException_WhenIdIsNull() {
        // 模拟行为：当传入null时，抛出IllegalArgumentException
        doThrow(new IllegalArgumentException("ID cannot be null")).when(mapper).deleteById((Serializable) null);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
        verify(mapper, times(1)).deleteById((Serializable) null);
    }

    // 边界测试：删除最小ID值
    @Test
    void delete_ShouldCallMapper_WhenIdIsMinValue() {
        Long minId = Long.MIN_VALUE;

        service.delete(minId);

        verify(mapper, times(1)).deleteById(minId);
    }
}