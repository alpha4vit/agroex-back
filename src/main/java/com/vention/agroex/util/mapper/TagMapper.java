package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Tag;
import com.vention.agroex.entity.TagEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagEntity toEntity(Tag tag);

    Tag toDto(TagEntity tagEntity);

    List<Tag> toDtos(List<TagEntity> tagEntities);

    List<TagEntity> toEntities(List<Tag> tags);
}
