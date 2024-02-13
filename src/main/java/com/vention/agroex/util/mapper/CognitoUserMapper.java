package com.vention.agroex.util.mapper;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.vention.agroex.dto.User;
import com.vention.agroex.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class CognitoUserMapper {

    public UserEntity convertCognitoUserToUserEntity(UserType awsCognitoUser) {
        UserEntity.UserEntityBuilder builder = UserEntity.builder();
        mapUserAttributesToUserEntity(awsCognitoUser.getAttributes(), builder);
        builder.creationDate(
                awsCognitoUser.getUserCreateDate().toInstant());
        builder.enabled(awsCognitoUser.getEnabled());
        return builder.build();
    }

    public UserEntity convertAdminGetUserResultToUserEntity(AdminGetUserResult adminGetUserResult){
        UserEntity.UserEntityBuilder builder = UserEntity.builder();
        mapUserAttributesToUserEntity(adminGetUserResult.getUserAttributes(), builder);
        builder.creationDate(
                adminGetUserResult.getUserCreateDate().toInstant());
        builder.enabled(adminGetUserResult.getEnabled());
        return builder.build();
    }

    private void mapUserAttributesToUserEntity(List<AttributeType> attributeList, UserEntity.UserEntityBuilder builder){
        for (AttributeType userAttribute : attributeList) {
            switch (userAttribute.getName()) {
                case "sub" -> builder.id(UUID.fromString(userAttribute.getValue()));
                case "name" -> builder.username(userAttribute.getValue());
                case "email" -> builder.email(userAttribute.getValue());
                case "custom:avatar" -> builder.avatar(userAttribute.getValue());
                case "zoneinfo" -> builder.timeZone(ZoneId.of(userAttribute.getValue()));
                case "email_verified" -> builder.emailVerified(Boolean.valueOf(userAttribute.getValue()));
            }
        }
    }

}
