package com.vention.agroex.service;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> getAll();

    UserEntity getById(Long id);

    UserEntity save(UserEntity userEntity);

    void deleteById(Long id);

    UserEntity update(Long id, UserEntity source);

    UserEntity uploadAvatar(Long id, Image avatar);

}
