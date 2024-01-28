package com.vention.agroex.util.validator;

import com.vention.agroex.dto.UserDTO;
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
public class UserDTOCreateValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;
        Map<String, String> errorsMap = new HashMap<>();
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
            errorsMap.put("username", "User with this username already exists!");
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            errorsMap.put("email", "User with this email already exists!");
        if (userRepository.findByPhoneNumber(userDTO.getPhoneNumber()).isPresent())
            errorsMap.put("phone_number", "User with this phone number already exists!");
        errors.getFieldErrors()
                .forEach(fieldError ->
                        errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        if (errorsMap.size() > 0){
            throw new InvalidArgumentException(errorsMap, "Invalid arguments!");
        }

    }

}
