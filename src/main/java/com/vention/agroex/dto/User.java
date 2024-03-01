package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String username;

    @Email
    @NotBlank(message = "Email cant be blank")
    private String email;

    @NotNull(message = "Time zone cant be null!")
    private ZoneId zoneinfo;

    private String avatar;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime creationDate;

    private Boolean emailVerified;

    private Boolean enabled;

}
