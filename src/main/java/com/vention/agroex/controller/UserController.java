package com.vention.agroex.controller;

import com.vention.agroex.dto.Image;
import com.vention.agroex.dto.User;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.mapper.UserMapper;
import com.vention.agroex.util.validator.UserCreateValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User controller")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final UserCreateValidator userCreateValidator;


    @GetMapping
    public ResponseEntity<Object> getAll() {
        var users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") UUID id) {
        var userEntity = userService.getById(id);
        return ResponseEntity.ok(userMapper.toDTO(userEntity));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user,
                                           BindingResult bindingResult) {
        userCreateValidator.validate(user, bindingResult);
        var saved = userService.save(userMapper.toEntity(user));
        return ResponseEntity.ok(userMapper.toDTO(saved));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UUID> deleteUserById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @PreAuthorize("@customSecurityExpression.isAuthenticatedUser(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") UUID id,
                                           @RequestBody @Valid User user) {
        var saved = userService.update(id, userMapper.toEntity(user));
        return ResponseEntity.ok(userMapper.toDTO(saved));
    }

    @PreAuthorize("@customSecurityExpression.isAuthenticatedUser(#id)")
    @PostMapping("/{id}/avatar")
    public ResponseEntity<User> uploadAvatar(@PathVariable("id") UUID id,
                                             @RequestParam("file") MultipartFile file) {
        var avatar = new Image(file);
        var user = userService.uploadAvatar(id, avatar);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @GetMapping("/updatedb")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<User>> updateDatabase() {
        return ResponseEntity.ok(userMapper.toDtos(userService.updateTable()));
    }

    @PreAuthorize("@customSecurityExpression.isAdmin()")
    @PostMapping("/{id}/enable")
    public ResponseEntity<User> enableUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userMapper.toDTO(userService.enable(id)));
    }

}
