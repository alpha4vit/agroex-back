package com.vention.agroex.model;

public interface CountryReportModel {
    Long getId();
    String getName();
    Double getPrice_sum();
    Double getTotal_bet_sum();
    Long getTotal_lot_quantity();
    Long getTotal_owners_quantity();
    Long getTotal_participant_count();
}
