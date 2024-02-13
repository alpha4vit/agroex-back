package com.vention.agroex.controller;

import com.vention.agroex.dto.User;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping("/register/{id}")
    public ResponseEntity<User> register(@PathVariable("id") UUID id) {
        var saved = userService.saveWithCheck(id);
        if (saved.isEmpty())
            return ResponseEntity.ok(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(saved.get()));
    }

}
