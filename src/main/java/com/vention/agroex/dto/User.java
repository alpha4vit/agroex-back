package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;

    @NotBlank(message = "Username cant be blank")
    @Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    private String username;

    @Email
    @NotBlank(message = "Email cant be blank")
    private String email;

    private String avatar;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime creationDate;

    private Boolean emailVerified;

    private ZoneId timeZone;

}
