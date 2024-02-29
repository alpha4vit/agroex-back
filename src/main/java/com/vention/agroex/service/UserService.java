package com.vention.agroex.service;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<UserEntity> getAll();

    UserEntity getById(UUID id);

    UserEntity save(UserEntity userEntity);

    Optional<UserEntity> saveWithCheck(UUID userId);

    void deleteById(UUID id);

    UserEntity update(UUID id, UserEntity source);

    UserEntity uploadAvatar(UUID id, Image avatar);

    void updateTable();

    UserEntity getAuthenticatedUser();

    void disable(UUID id);

    void enable(UUID id);

}
