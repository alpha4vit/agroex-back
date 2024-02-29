package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.repository.UserRepository;
import com.vention.agroex.service.AwsCognitoService;
import com.vention.agroex.service.ImageServiceStorage;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ImageServiceStorage imageServiceStorage;
    private final UserMapper userMapper;
    private final AwsCognitoService awsCognitoService;

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with this id not found!"));
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        userEntity.setEmailVerified(userEntity.getEmailVerified() != null ? userEntity.getEmailVerified() : false);
        return userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> saveWithCheck(UUID userId) {
        if (userRepository.existsById(userId))
            return Optional.empty();
        var fetched = awsCognitoService.getById(userId);
        return Optional.of(save(fetched));
    }

    @Override
    public void deleteById(UUID id) {
        var userEntity = getById(id);
        userRepository.delete(userEntity);
    }

    @Override
    public UserEntity update(UUID id, UserEntity source) {
        var userEntity = userMapper.update(getById(id), source);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity uploadAvatar(UUID id, Image avatar) {
        var user = getById(id);
        var saved = imageServiceStorage.uploadToStorage(avatar);
        user.setAvatar(saved);
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateTable() {
        userRepository.deleteAll();
        var userEntities = awsCognitoService.updateDb();
        userRepository.saveAll(userEntities);
    }


}
