package com.vention.agroex.service;

import com.vention.agroex.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface AwsCognitoService {

    UserEntity getById(UUID id);

    void setEnabled(UUID id, boolean value);

    List<UserEntity> updateDb();
}
