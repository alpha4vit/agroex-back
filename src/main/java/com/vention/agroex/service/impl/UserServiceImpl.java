package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.repository.LotRepository;
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
    private final LotRepository lotRepository;

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
    public List<UserEntity> updateTable() {
        var userEntities = awsCognitoService.updateDb();
        List<UserEntity> saved = userRepository.saveAll(userEntities);
        saved.forEach(user -> {
            if (!user.getEnabled())
                rejectLotsDueToUserDeactivation(lotRepository.findByUser(user));
        });
        return saved;
    }

    @Override
    public UserEntity getAuthenticatedUser() {
        var jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        var uuid = UUID.fromString(jwt.getClaimAsString("sub"));
        return getById(uuid);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserEntity enable(UUID id) {
        var user = getById(id);
        var beforeState = user.getEnabled();
        user.setEnabled(!beforeState);
        awsCognitoService.setEnabled(id, !beforeState);
        if (beforeState)
            rejectLotsDueToUserDeactivation(user.getLots());
        return userRepository.save(user);
    }

    protected void rejectLotsDueToUserDeactivation(List<LotEntity> lotEntities) {
        lotEntities.stream()
                .filter(lot -> !lot.getStatus().equals(StatusConstants.REJECTED_BY_ADMIN))
                .forEach(lot -> {
                    lot.setInnerStatus(StatusConstants.REJECTED_BY_ADMIN);
                    lot.setStatus(StatusConstants.INACTIVE);
                    lot.setAdminComment(!lot.getAdminComment().isEmpty() ?
                            String.format("%s. Rejected due to user deactivation", lot.getAdminComment()) :
                            "Rejected due to user deactivation");
                });

    }
}
