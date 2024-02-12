package com.vention.agroex.controller;

import com.vention.agroex.dto.User;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.mapper.UserMapper;
import com.vention.agroex.util.validator.UserDTOCreateValidator;
import com.vention.agroex.util.validator.UserDTOUpdateValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User controller")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserDTOCreateValidator userDTOCreateValidator;
    private final UserDTOUpdateValidator userDTOUpdateValidator;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userMapper.toDtos(userService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") Long id) {
        var userEntity = userService.getById(id);
        return ResponseEntity.ok(userMapper.toDTO(userEntity));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user,
                                           BindingResult bindingResult) {
        userDTOCreateValidator.validate(user, bindingResult);
        var saved = userService.save(userMapper.toEntity(user));
        return ResponseEntity.ok(userMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> deleteUserById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id,
                                           @RequestBody @Valid User user,
                                           BindingResult bindingResult) {
        user.setId(id);
        userDTOUpdateValidator.validate(user, bindingResult);
        var saved = userService.update(id, userMapper.toEntity(user));
        return ResponseEntity.ok(userMapper.toDTO(saved));
    }



}
