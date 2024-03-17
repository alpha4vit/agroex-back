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
import org.apache.poi.util.IOUtils;
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
    public byte[] baseLotReport(ReportRequest reportRequest) {
        List<LotReportModel> lotReportModels = lotRepository.baseLotFilter(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        excelWriter.writeLotsReport(lotReportModels);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/lotReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] lotReportByMaxPrice(ReportRequest reportRequest) {
        List<LotReportModel> lotReportModels = lotRepository.filterByPrice(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        excelWriter.writeLotsReport(lotReportModels);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/lotReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] userReportByLotCount(ReportRequest reportRequest) {
        List<UserReportModel> userReportModels = userRepository.filterByLotCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        excelWriter.writeUsersReport(userReportModels, ReportType.USER_MAX_LOT_QUANTITY);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/userReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] userReportOwnersByBets(ReportRequest reportRequest) {
        List<UserReportModel> userReportModels = userRepository.filterOwnerByBetMoney(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        excelWriter.writeUsersReport(userReportModels, ReportType.USER_MAX_BET_AMOUNT);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/userReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] userReportParticipantByBets(ReportRequest reportRequest) {
        List<UserReportModel> userReportModels = userRepository.filterParticipantByBets(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType(),
                reportRequest.countryId()
        );
        excelWriter.writeUsersReport(userReportModels, ReportType.USER_MAX_MONEY_IN_LOTS);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/userReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] countryReportByLotPrice(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByLotPrice(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOTS_TOTAL_BET_SUM);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/countryReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] countryReportByLotCount(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByLotCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOTS_TOTAL_QUANTITY);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/countryReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] countryReportByOwnerCount(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByOwnerCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOTS_TOTAL_OWNERS_QUANTITY);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/countryReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] countryReportByOwnersLotsBets(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByOwnersLotsBets(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOTS_TOTAL_MONEY_NESTED);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/countryReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] countryReportByParticipantCount(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByParticipantCount(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOTS_TOTAL_MONEY_NESTED);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/countryReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] countryReportByParticipantBets(ReportRequest reportRequest) {
        List<CountryReportModel> countryReportModels = countryRepository.filterByParticipantBets(
                reportRequest.actualStartDate(),
                reportRequest.expirationDate(),
                reportRequest.lotType()
        );
        excelWriter.writeCountriesReport(countryReportModels, ReportType.COUNTRY_LOTS_TOTAL_MONEY_NESTED);
        try (var is = getClass().getClassLoader().getResourceAsStream("reports/countryReport.xlsx")){
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
