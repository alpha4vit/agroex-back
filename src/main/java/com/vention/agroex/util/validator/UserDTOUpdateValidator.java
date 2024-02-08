package com.vention.agroex.util.validator;

import com.vention.agroex.dto.User;
import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserDTOUpdateValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        UserEntity beforeUserEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with this id not found!"));
        Map<String, String> errorsMap = new HashMap<>();

        userRepository.findByUsernameOrEmailOrPhoneNumber(user.getUsername(), user.getEmail(), user.getPhoneNumber())
                .forEach(userEntity -> {
                    if (!beforeUserEntity.getUsername().equals(user.getUsername()) && userEntity.getUsername().equals(user.getUsername()))
                        errorsMap.put("username", "User with this username already exists!");
                    if (!beforeUserEntity.getEmail().equals(user.getEmail()) && userEntity.getEmail().equals(user.getEmail()))
                        errorsMap.put("email", "User with this email already exists!");
                    if (!beforeUserEntity.getPhoneNumber().equals(user.getPhoneNumber()) && userEntity.getPhoneNumber().equals(user.getPhoneNumber()))
                        errorsMap.put("phone_number", "User with this phone number already exists!");
                });

        errors.getFieldErrors()
                .forEach(fieldError ->
                {
                    if (fieldError.getField().equals("password")) {
                        if (user.getPassword() != null && !user.getPassword().isBlank())
                            errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    }
                    else
                        errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());

                });

        if (!errorsMap.isEmpty()){
            throw new InvalidArgumentException(errorsMap, "Invalid arguments!");
        }
    }

}
