package com.vention.agroex.controller;

import com.vention.agroex.dto.UserDTO;
import com.vention.agroex.entity.User;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.mapper.UserMapper;
import com.vention.agroex.util.validator.UserValidator;
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
    private final UserValidator userValidator;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userMapper.toDtos(userService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO,
                                              BindingResult bindingResult) {
        userValidator.validate(userDTO, bindingResult);
        User entity = userMapper.toEntity(userDTO);
        entity = userService.save(entity);
        return ResponseEntity.ok(userMapper.toDTO(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> deleteUserById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id,
                                              @RequestBody @Valid UserDTO userDTO,
                                              BindingResult bindingResult) {
        userValidator.validate(userDTO, bindingResult);
        User entity = userService.getById(id);
        entity = userMapper.update(entity, userDTO);
        entity = userService.update(entity);
        return ResponseEntity.ok(userMapper.toDTO(entity));
    }



}
