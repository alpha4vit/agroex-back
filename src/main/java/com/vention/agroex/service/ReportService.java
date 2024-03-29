package com.vention.agroex.service;

import com.vention.agroex.model.ReportRequest;
import org.springframework.core.io.Resource;

public interface ReportService {

    Resource baseLotReport(ReportRequest reportRequest);

    Resource lotReportByMaxPrice(ReportRequest reportRequest);

    Resource userReportByLotCount(ReportRequest reportRequest);

    Resource userReportOwnersByBets(ReportRequest reportRequest);

    Resource userReportParticipantByBets(ReportRequest reportRequest);

    Resource countryReportByLotPrice(ReportRequest reportRequest);

    Resource countryReportByLotCount(ReportRequest reportRequest);

    Resource countryReportByOwnerCount(ReportRequest reportRequest);

    Resource countryReportByOwnersLotsBets(ReportRequest reportRequest);

    Resource countryReportByParticipantCount(ReportRequest reportRequest);

    Resource countryReportByParticipantBets(ReportRequest reportRequest);

}
