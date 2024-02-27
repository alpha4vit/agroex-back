package com.vention.agroex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bet {

    private Long id;

    private Long lotId;

    private Float amount;

    private Long userId;
}
