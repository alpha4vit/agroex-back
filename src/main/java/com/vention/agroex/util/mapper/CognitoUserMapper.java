package com.vention.agroex.util.mapper;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.vention.agroex.dto.User;
import com.vention.agroex.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
public class CognitoUserMapper {

    public User convertCognitoUserToUser(UserType awsCognitoUser) {

        User.UserBuilder builder = User.builder();
        awsCognitoUser.getAttributes().forEach(el -> System.out.println(el.getName()));
        for (AttributeType userAttribute : awsCognitoUser.getAttributes()) {
            switch (userAttribute.getName()) {
                case "sub" -> builder.id(UUID.fromString(userAttribute.getValue()));
                case "name" -> builder.username(userAttribute.getValue());
                case "email" -> builder.email(userAttribute.getValue());
                case "custom:avatar" -> builder.avatar(userAttribute.getValue());
                case "zoneinfo" -> builder.timeZone(ZoneId.of(userAttribute.getValue()));
                case "email_verified" -> builder.emailVerified(Boolean.valueOf(userAttribute.getValue()));
            }
        }

        return builder.build();
    }

    public UserEntity convertCognitoUserToUserEntity(UserType awsCognitoUser) {
        UserEntity.UserEntityBuilder builder = UserEntity.builder();
        mapUserAttributesToUserEntity(awsCognitoUser.getAttributes(), builder);
        builder.creationDate(
                awsCognitoUser.getUserCreateDate().toInstant());
        return builder.build();
    }

    public UserEntity convertAdminGetUserResultToUserEntity(AdminGetUserResult adminGetUserResult){
        UserEntity.UserEntityBuilder builder = UserEntity.builder();
        mapUserAttributesToUserEntity(adminGetUserResult.getUserAttributes(), builder);
        builder.creationDate(
                adminGetUserResult.getUserCreateDate().toInstant());
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
