package com.vention.agroex.security;

import com.vention.agroex.service.LotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component("customSecurityExpression")
public class CustomSecurityExpression {

    private final LotService lotService;

    public boolean isLotOwner(Long lotId) {
        var jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        var uuid = UUID.fromString(jwt.getClaimAsString("sub"));
        var lotEntity = lotService.getById(lotId);
        return lotEntity.getUser().getId().equals(uuid);
    }

}
