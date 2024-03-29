package com.vention.agroex.service;

import com.vention.agroex.model.CountryReportModel;
import com.vention.agroex.model.LotReportModel;
import com.vention.agroex.model.UserReportModel;
import com.vention.agroex.util.constant.ReportType;
import org.springframework.core.io.Resource;

import java.util.List;

public interface ExcelWriter {
    Resource writeLotsReport(List<LotReportModel> lotReportModels);

    Resource writeUsersReport(List<UserReportModel> userReportModels, ReportType reportType);

    Resource writeCountriesReport(List<CountryReportModel> countryReportModels, ReportType reportType);
}
