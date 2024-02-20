package com.vention.agroex.service;

import com.vention.agroex.entity.TagEntity;

import java.util.List;

public interface TagService {

    TagEntity getById(Long id);

    List<TagEntity> getAll();

    TagEntity save(TagEntity tagEntity);

    TagEntity update(Long id, TagEntity tagEntity);

    void delete(TagEntity tagEntity);

    void deleteById(Long id);
}
