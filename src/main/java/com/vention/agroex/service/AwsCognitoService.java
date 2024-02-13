package com.vention.agroex.service;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.vention.agroex.dto.User;
import com.vention.agroex.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface AwsCognitoService {

    List<User> getAll();

    UserEntity getById(UUID id);

    User setEnabled(String username, boolean value);

    List<UserEntity> updateDb();
}
