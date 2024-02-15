package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.repository.UserRepository;
import com.vention.agroex.service.ImageServiceStorage;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ImageServiceStorage imageServiceStorage;
    private final UserMapper userMapper;

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with this id not found!"));
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        userEntity.setEmailVerified(false);
        return userRepository.save(userEntity);
    }

    @Override
    public void deleteById(Long id) {
        var userEntity = getById(id);
        userRepository.delete(userEntity);
    }

    @Override
    public UserEntity update(Long id, UserEntity source) {
        var userEntity = userMapper.update(getById(id), source);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity uploadAvatar(Long id, Image avatar) {
        var user = getById(id);
        var saved = imageServiceStorage.uploadToStorage(avatar);
        user.setAvatar(saved);
        return userRepository.save(user);
    }

}
