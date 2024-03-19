package com.vention.agroex.service;

import com.vention.agroex.model.ReportRequest;

public interface ReportService {

    byte[] baseLotReport(ReportRequest reportRequest);

    byte[] lotReportByMaxPrice(ReportRequest reportRequest);

    byte[] userReportByLotCount(ReportRequest reportRequest);

    byte[] userReportOwnersByBets(ReportRequest reportRequest);

    byte[] userReportParticipantByBets(ReportRequest reportRequest);

    byte[] countryReportByLotPrice(ReportRequest reportRequest);

    byte[] countryReportByLotCount(ReportRequest reportRequest);

    byte[] countryReportByOwnerCount(ReportRequest reportRequest);

    byte[] countryReportByOwnersLotsBets(ReportRequest reportRequest);

    byte[] countryReportByParticipantCount(ReportRequest reportRequest);

    byte[] countryReportByParticipantBets(ReportRequest reportRequest);

}
