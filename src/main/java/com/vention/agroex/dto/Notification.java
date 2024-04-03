package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vention.agroex.util.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private UUID id;

    private UUID userId;

    private Long lotId;

    private String type;

    private String title;

    private String message;

    private String readStatus;

    private Instant sendTime;

    @JsonIgnore
    private Role role;
}
