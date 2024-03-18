package com.vention.agroex.service.impl;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminEnableUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.exception.UserChangeException;
import com.vention.agroex.props.AwsProperties;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.repository.UserRepository;
import com.vention.agroex.service.AwsCognitoService;
import com.vention.agroex.util.constant.StatusConstants;
import com.vention.agroex.util.mapper.CognitoUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsCognitoServiceImpl implements AwsCognitoService {

    private final AwsProperties awsProperties;
    private final CognitoUserMapper cognitoUserMapper;
    private final AWSCognitoIdentityProvider identityProvider;
    private final LotRepository lotRepository;
    private final UserRepository userRepository;

    @Override
    public UserEntity getById(UUID id) {
        var request = new AdminGetUserRequest();
        request.withUserPoolId(awsProperties.getUserPoolId());
        request.withUsername(id.toString());
        var response = identityProvider.adminGetUser(request);
        return cognitoUserMapper.convertAdminGetUserResultToUserEntity(response);
    }

    @Override
    public void setEnabled(UUID id, boolean value) {
        if (value) {
            var request = new AdminEnableUserRequest();
            request.withUsername(id.toString());
            request.withUserPoolId(awsProperties.getUserPoolId());
            identityProvider.adminEnableUser(request);
        } else {
            var user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("There is no user with this id"));
            if (lotRepository.existsByUser(user)) {
                throw new UserChangeException("This user has lots, delete them firstly");
            }
            if (lotRepository.existsByBetsUserIdAndStatus(user.getId(), StatusConstants.ACTIVE)) {
                throw new UserChangeException("This user has bets or deals in active lots, you cant deactivate him");
            }
            var request = new AdminDisableUserRequest();
            request.withUserPoolId(awsProperties.getUserPoolId());
            request.withUsername(id.toString());
            identityProvider.adminDisableUser(request);
        }
    }

    @Override
    public List<UserEntity> updateDb() {
        var request = new ListUsersRequest();
        request.withUserPoolId(awsProperties.getUserPoolId());
        var result = identityProvider.listUsers(request);
        return result.getUsers().stream().map(cognitoUserMapper::convertCognitoUserToUserEntity).toList();
    }

}
