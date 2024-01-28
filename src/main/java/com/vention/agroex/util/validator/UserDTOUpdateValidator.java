package com.vention.agroex.util.validator;

import com.vention.agroex.dto.UserDTO;
import com.vention.agroex.entity.User;
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
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;
        User beforeUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with this id not found!"));
        Map<String, String> errorsMap = new HashMap<>();
        if (!beforeUser.getUsername().equals(userDTO.getUsername()) && userRepository.findByUsername(userDTO.getUsername()).isPresent())
            errorsMap.put("username", "User with this username already exists!");
        if (!beforeUser.getEmail().equals(userDTO.getEmail()) && userRepository.findByEmail(userDTO.getEmail()).isPresent())
            errorsMap.put("email", "User with this email already exists!");
        if (!beforeUser.getPhoneNumber().equals(userDTO.getPhoneNumber()) && userRepository.findByPhoneNumber(userDTO.getPhoneNumber()).isPresent())
            errorsMap.put("phone_number", "User with this phone number already exists!");
        errors.getFieldErrors()
                .forEach(fieldError ->
                {
                    if (fieldError.getField().equals("password")) {
                        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank())
                            errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    }
                    else
                        errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());

                });
        if (errorsMap.size() > 0){
            throw new InvalidArgumentException(errorsMap, "Invalid arguments!");
        }
    }

}
