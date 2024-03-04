package com.vention.agroex.service.impl;

import com.vention.agroex.model.CountryReportModel;
import com.vention.agroex.model.LotReportModel;
import com.vention.agroex.model.UserReportModel;
import com.vention.agroex.util.constant.ReportType;

import java.util.List;

public interface ExcelWriter {
    void writeLotsReport(List<LotReportModel> lotReportModels);

    void writeUsersReport(List<UserReportModel> userReportModels, ReportType reportType);

    void writeCountriesReport(List<CountryReportModel> countryReportModels, ReportType reportType);
}
