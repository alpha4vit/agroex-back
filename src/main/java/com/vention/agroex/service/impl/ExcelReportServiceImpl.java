package com.vention.agroex.service.impl;

import com.vention.agroex.model.CountryReportModel;
import com.vention.agroex.model.LotReportModel;
import com.vention.agroex.model.ReportRequest;
import com.vention.agroex.model.UserReportModel;
import com.vention.agroex.repository.CountryRepository;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.repository.UserRepository;
import com.vention.agroex.service.ExcelWriter;
import com.vention.agroex.service.ReportService;
import com.vention.agroex.util.constant.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelReportServiceImpl implements ReportService {

    private final LotRepository lotRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final ExcelWriter excelWriter;

    @Override
    public Resource baseLotReport(ReportRequest reportRequest) {
        List<LotReportModel> lotReportModels = lotRepository.baseLotFilter(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        return excelWriter.writeLotsReport(lotReportModels);
    }

    @Override
    public Resource lotReportByMaxPrice(ReportRequest reportRequest) {
        List<LotReportModel> lotReportModels = lotRepository.filterByPrice(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        return excelWriter.writeLotsReport(lotReportModels);
    }

    @Override
    public Resource userReportByLotCount(ReportRequest reportRequest) {
        List<UserReportModel> userReportModels = userRepository.filterByLotCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        return excelWriter.writeUsersReport(userReportModels, ReportType.USER_MAX_LOT_QUANTITY);
    }

    @Override
    public Resource userReportOwnersByBets(ReportRequest reportRequest) {
        List<UserReportModel> userReportModels = userRepository.filterOwnerByBetMoney(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        return excelWriter.writeUsersReport(userReportModels, ReportType.USER_MAX_BET_AMOUNT);
    }

    @Override
    public Resource userReportParticipantByBets(ReportRequest reportRequest) {
        List<UserReportModel> userReportModels = userRepository.filterParticipantByBets(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        return excelWriter.writeUsersReport(userReportModels, ReportType.USER_MAX_MONEY_IN_LOTS);
    }

    @Override
    public Resource countryReportByLotPrice(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByLotPrice(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        return excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOT_PRICE);
    }

    @Override
    public Resource countryReportByLotCount(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByLotCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        return excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOT_COUNT);
    }

    @Override
    public Resource countryReportByOwnerCount(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByOwnerCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        return excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_OWNER_COUNT);
    }

    @Override
    public Resource countryReportByOwnersLotsBets(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByOwnersLotsBets(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        return excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_OWNER_BETS);
    }

    @Override
    public Resource countryReportByParticipantCount(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByParticipantCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        return excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_PARTICIPANT_COUNT);
    }

    @Override
    public Resource countryReportByParticipantBets(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByParticipantBets(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        return excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_PARTICIPANT_BETS);
    }
}
