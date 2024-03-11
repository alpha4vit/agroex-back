package com.vention.agroex.util.updater;

import com.vention.agroex.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdater {

    private final UserService userService;

    @Scheduled(fixedRate = 300000)
    @Transactional(rollbackOn = Exception.class)
    public void synchronizeUsersDatabaseWithCognito(){
        log.info("Scheduled users table synchronizing with Cognito user pool started!");
        userService.updateTable();
        log.info("Scheduled users table synchronizing with Cognito user pool finished!");
    }

}
