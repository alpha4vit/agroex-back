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

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserCreateValidator userCreateValidator;


    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        var users = userService.getAll();
        return ResponseEntity.ok(userMapper.toDtos(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") UUID id) {
        var userEntity = userService.getById(id);
        return ResponseEntity.ok(userMapper.toDTO(userEntity));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user,
                                           BindingResult bindingResult) {
        userCreateValidator.validate(user, bindingResult);
        var saved = userService.save(userMapper.toEntity(user));
        return ResponseEntity.ok(userMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UUID> deleteUserById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") UUID id,
                                           @RequestBody User user) {
        var saved = userService.update(id, userMapper.toEntity(user));
        return ResponseEntity.ok(userMapper.toDTO(saved));
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<User> uploadAvatar(@PathVariable("id") UUID id,
                                                @RequestParam("file") MultipartFile file){
        var avatar = new Image(file);
        var user = userService.uploadAvatar(id, avatar);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/updatedb")
    @ResponseStatus(HttpStatus.OK)
    public void udpateDatabase(){
        userService.updateTable();
    }

    @PostMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.OK)
    public void disableUser(@PathVariable("id") UUID id){
        userService.disable(id);
    }

    @PostMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.OK)
    public void enableUser(@PathVariable("id") UUID id){
        userService.enable(id);
    }

}
