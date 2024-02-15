package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @NotBlank(message = "Username cant be blank")
    @Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    private String username;

    @Email
    @NotBlank(message = "Email cant be blank")
    private String email;

    @NotBlank(message = "Password can`t be blank")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 64, message = "Password must contain between 8 and 64 characters")
    private String password;

    @NotBlank(message = "Phone number can`t be blank")
    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant creationDate;

    private Boolean emailVerified;

    private String avatar;

}
