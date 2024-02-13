package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.repository.UserRepository;
import com.vention.agroex.service.AwsCognitoService;
import com.vention.agroex.service.ImageServiceStorage;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.constant.StatusConstants;
import com.vention.agroex.util.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
        var userEntities = awsCognitoService.updateDb();
        userRepository.saveAll(userEntities);
    }

    @Override
    public UserEntity getAuthenticatedUser() {
        var jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        var uuid = UUID.fromString(jwt.getClaimAsString("sub"));
        return getById(uuid);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void disable(UUID id) {
        var user = getById(id);
        user.setEnabled(false);
        for (var lot : user.getLots()){
            lot.setStatus(StatusConstants.REJECTED_BY_ADMIN);
            lot.setAdminComment(String.format("%s. Rejected due to user deactivation", lot.getAdminComment()));
        }
        awsCognitoService.setEnabled(id, false);
    }

    @Override
    public void enable(UUID id) {
        var user = getById(id);
        user.setEnabled(true);
        awsCognitoService.setEnabled(id, true);
        userRepository.save(user);
    }

}
