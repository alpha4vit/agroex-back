package com.vention.agroex.model;

import java.time.Instant;
import java.util.UUID;

public interface LotReportModel {
    Long getId();
    Instant getCreation_date();
    String getTitle();
    Instant getExpiration_date();
    String getLot_type();
    Double getOriginal_price();
    String getOriginal_currency();
    String getLocation_country();
    String getLocation_region();
    String getProduct_category_title();
    Double getOriginal_min_price();
    String getInner_status();
    String getUser_status();
    String getStatus();
    Long getDuration();
    UUID getUser_id();
    String getUser_email();
    Instant getActual_start_date();
    String getAdmin_comment();
}
