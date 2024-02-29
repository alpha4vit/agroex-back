package com.vention.agroex.util.validator;

import com.vention.agroex.dto.User;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserCreateValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        Map<String, String> errorsMap = new HashMap<>();
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            errorsMap.put("username", "User with this username already exists!");
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            errorsMap.put("email", "User with this email already exists!");
        errors.getFieldErrors()
                .forEach(fieldError ->
                        errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        if (errorsMap.size() > 0){
            throw new InvalidArgumentException(errorsMap, "Invalid arguments!");
        }

    }

}
