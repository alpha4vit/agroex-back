package com.vention.agroex.service.impl;

import com.vention.agroex.entity.TagEntity;
import com.vention.agroex.repository.TagRepository;
import com.vention.agroex.service.ColorService;
import com.vention.agroex.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ColorService colorService;

    @Override
    public TagEntity getById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag with this id not found!"));
    }

    @Override
    public List<TagEntity> getAll() {
        return tagRepository.findAll();
    }

    @Override
    public TagEntity save(TagEntity tagEntity) {
        tagEntity.setColor(colorService.getNextColor());
        tagEntity.setTitle(StringUtils.normalizeSpace(tagEntity.getTitle()));
        return tagRepository.save(tagEntity);
    }

    @Override
    public List<TagEntity> saveList(List<TagEntity> tagEntities) {
        tagEntities.forEach(tag -> {
                    tag.setColor(colorService.getNextColor());
                    tag.setTitle(StringUtils.normalizeSpace(tag.getTitle()));
                }
        );
        return tagRepository.saveAll(tagEntities);
    }

    @Override
    public TagEntity update(Long id, TagEntity tagEntity) {
        tagEntity.setId(id);
        return tagRepository.save(tagEntity);
    }

    @Override
    public void delete(TagEntity tagEntity) {
        tagRepository.delete(tagEntity);
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        tagRepository.deleteById(id);
    }


}
