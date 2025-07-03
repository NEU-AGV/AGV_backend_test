package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.mapper.StreamSourceMapper;
import com.moxin.agvbackend.pojo.entity.StreamSource;
import com.moxin.agvbackend.service.StreamSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StreamSourceServiceImpl implements StreamSourceService {

    @Autowired
    private StreamSourceMapper mapper;

    @Override
    public List<StreamSource> listAll() {
        return mapper.selectList(null);
    }

    @Override
    public StreamSource getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public void add(StreamSource source) {
        source.setCreatedAt(LocalDateTime.now());
        source.setUpdatedAt(LocalDateTime.now());
        mapper.insert(source);
    }

    @Override
    public void update(StreamSource source) {
        source.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(source);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
