package com.vention.agroex.service;

import com.vention.agroex.model.ReportRequest;

public interface ReportService {

    byte[] baseLotReport(ReportRequest reportRequest);

    byte[] lotReportByMaxPrice(ReportRequest reportRequest);

    byte[] userReportByLotCount(ReportRequest reportRequest);

    byte[] userReportByBetMoney(ReportRequest reportRequest);

    byte[] userReportByLotMoney(ReportRequest reportRequest);

    byte[] countryReportByBetMoney(ReportRequest reportRequest);

    byte[] countryReportByLotCount(ReportRequest reportRequest);

    byte[] countryReportByOwnerCount(ReportRequest reportRequest);

    byte[] countryReportByLotMoney(ReportRequest reportRequest);

    byte[] countryReportByParticipantCount(ReportRequest reportRequest);

}
