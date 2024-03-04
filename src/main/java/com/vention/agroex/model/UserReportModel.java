package com.vention.agroex.model;

import java.time.Instant;
import java.util.UUID;

public interface UserReportModel {
    UUID getId();
    Instant getCreation_date();
    String getUsername();
    String getEmail();
    Double getLot_quantity();
    Double getPrice_sum();
    Double getBet_amount();
}
