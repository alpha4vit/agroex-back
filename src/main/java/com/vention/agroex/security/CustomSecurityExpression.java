package com.vention.agroex.security;

import com.vention.agroex.service.LotService;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.constant.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component("customSecurityExpression")
public class CustomSecurityExpression {

    private final LotService lotService;
    private final UserService userService;

    @Cacheable(value = "lotEntity", key = "#lotId")
    public boolean isLotOwner(Long lotId) {
        var authUser = userService.getAuthenticatedUser();
        var lotEntity = lotService.getById(lotId);
        return lotEntity.getUser().getId().equals(authUser.getId());
    }

    public boolean isUserEnabled(){
        var user = userService.getAuthenticatedUser();
        return user.getEnabled();
    }

    public boolean isAdmin(){
        var user = userService.getAuthenticatedUser();
        return user.getRoles().contains(Role.ADMIN);
    }

    public boolean isAuthenticatedUser(UUID userId){
        var user = userService.getAuthenticatedUser();
        return user.getId().equals(userId);
    }

}
