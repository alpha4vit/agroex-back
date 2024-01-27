package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String username;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant registrationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean emailVerified;

}
