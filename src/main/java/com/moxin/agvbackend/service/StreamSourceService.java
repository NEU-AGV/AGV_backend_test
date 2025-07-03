package com.moxin.agvbackend.service;

import com.moxin.agvbackend.pojo.entity.StreamSource;

import java.util.List;

public interface StreamSourceService {
    List<StreamSource> listAll();

    StreamSource getById(Long id);

    void add(StreamSource source);

    void update(StreamSource source);

    void delete(Long id);
}
