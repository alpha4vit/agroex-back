package com.vention.agroex.service.impl;

import com.vention.agroex.model.CountryReportModel;
import com.vention.agroex.model.LotReportModel;
import com.vention.agroex.model.UserReportModel;
import com.vention.agroex.service.ExcelWriter;
import com.vention.agroex.util.constant.ExcelDataFormat;
import com.vention.agroex.util.constant.ReportType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
public class ExcelWriterImpl implements ExcelWriter {

    @Override
    public void writeLotsReport(List<LotReportModel> lotReportModels){
        try (var file = new FileOutputStream("src/main/resources/reports/lotReport.xlsx");
            var workbook = new XSSFWorkbook()){
            var sheet = workbook.createSheet("Report");
            writeTableHeaderForLots(workbook, sheet);
            var rowCounter = 1;
            for (var lot : lotReportModels){
                var row = sheet.createRow(rowCounter);
                rowCounter++;
                var idCell = row.createCell(0);
                idCell.setCellValue(lot.getId());

                var titleCell = row.createCell(1);
                titleCell.setCellValue(lot.getTitle());

                var lotTypeCell = row.createCell(2);
                lotTypeCell.setCellValue(lot.getLot_type());

                var ownerIdCell = row.createCell(3);
                ownerIdCell.setCellValue(lot.getUser_id().toString());

                var dateCellStyle = getCellStyle(workbook, ExcelDataFormat.DATE);

                var creationDateCell = row.createCell(4);
                creationDateCell.setCellValue(LocalDateTime.ofInstant(lot.getCreation_date(), ZoneId.systemDefault()));
                creationDateCell.setCellStyle(dateCellStyle);

                var expirationDateCell = row.createCell(5);
                expirationDateCell.setCellValue(LocalDateTime.ofInstant(lot.getExpiration_date(), ZoneId.systemDefault()));
                expirationDateCell.setCellStyle(dateCellStyle);

                var priceCell = row.createCell(6);
                priceCell.setCellValue(lot.getOriginal_price());

                var currencyCell = row.createCell(7);
                currencyCell.setCellValue(lot.getOriginal_currency());

                var lotUserStatusCell = row.createCell(8);
                lotUserStatusCell.setCellValue(lot.getUser_status());

                var globalStatusCell = row.createCell(9);
                globalStatusCell.setCellValue(lot.getStatus());

                var productCategoryCell = row.createCell(10);
                productCategoryCell.setCellValue(lot.getProduct_category_title());

                var locationCountryCell = row.createCell(11);
                locationCountryCell.setCellValue(lot.getLocation_country());

                var locationRegionCell = row.createCell(12);
                locationRegionCell.setCellValue(lot.getLocation_region());

                var durationCell = row.createCell(13);
                durationCell.setCellValue(lot.getDuration());

                var actualStartDateCell = row.createCell(14);
                if (lot.getActual_start_date() != null)
                    actualStartDateCell.setCellValue(LocalDateTime.ofInstant(lot.getActual_start_date(), ZoneId.systemDefault()));
                actualStartDateCell.setCellStyle(dateCellStyle);

                var adminCommentCell = row.createCell(15);
                adminCommentCell.setCellValue(lot.getAdmin_comment());

            }
            workbook.write(file);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new InternalError(e.getMessage());
        }
    }

    @Override
    public void writeUsersReport(List<UserReportModel> userReportModels, ReportType reportType) {
        try (var file = new FileOutputStream("src/main/resources/reports/userReport.xlsx");
             var workbook = new XSSFWorkbook()){
            var sheet = workbook.createSheet("Report");
            writeTableHeaderForUsers(workbook, sheet, reportType);
            var rowCounter = 1;
            for (var user : userReportModels){
                var row = sheet.createRow(rowCounter);
                rowCounter++;
                var idCell = row.createCell(0);
                idCell.setCellValue(user.getId().toString());

                var usernameCell = row.createCell(1);
                usernameCell.setCellValue(user.getUsername());

                var emailCell = row.createCell(2);
                emailCell.setCellValue(user.getEmail());

                var dateCellStyle = getCellStyle(workbook, ExcelDataFormat.DATE);

                var registrationDateCell = row.createCell(3);
                registrationDateCell.setCellValue(LocalDateTime.ofInstant(user.getCreation_date(), ZoneId.systemDefault()));
                registrationDateCell.setCellStyle(dateCellStyle);

                var optionalCell = row.createCell(4);
                switch (reportType){
                    case USER_MAX_BET_AMOUNT -> optionalCell.setCellValue(user.getBet_amount());
                    case USER_MAX_LOT_QUANTITY -> optionalCell.setCellValue(user.getLot_quantity());
                    case USER_MAX_MONEY_IN_LOTS -> optionalCell.setCellValue(user.getPrice_sum());
                }

            }
            workbook.write(file);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new InternalError(e.getMessage());
        }
    }

    @Override
    public void writeCountriesReport(List<CountryReportModel> countryReportModels, ReportType reportType) {
        try (var file = new FileOutputStream("src/main/resources/reports/countryReport.xlsx");
             var workbook = new XSSFWorkbook()){
            var sheet = workbook.createSheet("Report");
            writeTableHeaderForCountries(workbook, sheet, reportType);
            var rowCounter = 1;
            for (var country : countryReportModels){
                var row = sheet.createRow(rowCounter);
                rowCounter++;
                var idCell = row.createCell(0);
                idCell.setCellValue(country.getId());

                var nameCell = row.createCell(1);
                nameCell.setCellValue(country.getName());

                var optionalCell = row.createCell(2);
                switch (reportType){
                    case COUNTRY_LOTS_TOTAL_BET_SUM -> optionalCell.setCellValue(country.getTotal_bet_sum());
                    case COUNTRY_LOTS_TOTAL_MONEY_NESTED -> optionalCell.setCellValue(country.getTotal_bets_amount());
                    case COUNTRY_LOTS_TOTAL_QUANTITY -> optionalCell.setCellValue(country.getTotal_lot_quantity());
                    case COUNTRY_LOTS_TOTAL_OWNERS_QUANTITY -> optionalCell.setCellValue(country.getTotal_users_quantity());
                }

            }
            workbook.write(file);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new InternalError(e.getMessage());
        }
    }

    private void writeTableHeaderForLots(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < 20; ++i) {
            sheet.setColumnWidth(i, 4500);
            sheet.setDefaultColumnStyle(i, headerStyle);
        }

        var header = sheet.createRow(0);

        var idCell = header.createCell(0);
        idCell.setCellValue("Id");

        var titleCell = header.createCell(1);
        titleCell.setCellValue("Title");

        var lotTypeCell = header.createCell(2);
        lotTypeCell.setCellValue("Lot type");

        var ownerId = header.createCell(3);
        ownerId.setCellValue("Owner id");
        sheet.setColumnWidth(3, 10000);

        var creationDateCell = header.createCell(4);
        creationDateCell.setCellValue("Creation Date");

        var expirationDateCell = header.createCell(5);
        expirationDateCell.setCellValue("Expiration Date");

        var priceCell = header.createCell(6);
        priceCell.setCellValue("Price");

        var currencyCell = header.createCell(7);
        currencyCell.setCellValue("Currency");

        var lotUserStatusCell = header.createCell(8);
        lotUserStatusCell.setCellValue("Lot user status");

        var globalStatusCell = header.createCell(9);
        globalStatusCell.setCellValue("Global status");

        var productCategoryCell = header.createCell(10);
        productCategoryCell.setCellValue("Product category");

        var locationCountryCell = header.createCell(11);
        locationCountryCell.setCellValue("Country");

        var locationRegionCell = header.createCell(12);
        locationRegionCell.setCellValue("Region");

        var durationCell = header.createCell(13);
        durationCell.setCellValue("Duration");

        var actualStartDateCell = header.createCell(14);
        actualStartDateCell.setCellValue("Actual start date");

        var adminCommentCell = header.createCell(15);
        adminCommentCell.setCellValue("Admin comment");

        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:Q1"));
    }


    private void writeTableHeaderForUsers(Workbook workbook, Sheet sheet, ReportType reportType) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < 15; ++i) {
            sheet.setColumnWidth(i, 4500);
            sheet.setDefaultColumnStyle(i, headerStyle);
        }

        var header = sheet.createRow(0);

        var idCell = header.createCell(0);
        idCell.setCellValue("Id");
        sheet.setColumnWidth(0, 10000);

        var usernameCell = header.createCell(1);
        usernameCell.setCellValue("Username");
        sheet.setColumnWidth(1, 10000);

        var emailCell = header.createCell(2);
        emailCell.setCellValue("Email");
        sheet.setColumnWidth(2, 10000);

        var registrationDateCell = header.createCell(3);
        registrationDateCell.setCellValue("Registration date");

        var optionalCell = header.createCell(4);

        switch (reportType){
            case USER_MAX_BET_AMOUNT -> optionalCell.setCellValue("Total bet amount");
            case USER_MAX_LOT_QUANTITY -> optionalCell.setCellValue("Lot quantity");
            case USER_MAX_MONEY_IN_LOTS -> optionalCell.setCellValue("Money in lots");
        }

        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:F1"));
    }

    private void writeTableHeaderForCountries(Workbook workbook, Sheet sheet, ReportType reportType) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < 10; ++i) {
            sheet.setColumnWidth(i, 4500);
            sheet.setDefaultColumnStyle(i, headerStyle);
        }

        var header = sheet.createRow(0);

        var idCell = header.createCell(0);
        idCell.setCellValue("Id");

        var nameCell = header.createCell(1);
        nameCell.setCellValue("Name");

        var optionalCell = header.createCell(2);

        switch (reportType){
            case COUNTRY_LOTS_TOTAL_BET_SUM -> optionalCell.setCellValue("Total bet amount");
            case COUNTRY_LOTS_TOTAL_MONEY_NESTED -> optionalCell.setCellValue("Total money nested");
            case COUNTRY_LOTS_TOTAL_QUANTITY -> optionalCell.setCellValue("Total lots quantity");
            case COUNTRY_LOTS_TOTAL_OWNERS_QUANTITY -> optionalCell.setCellValue("Total lots owners quantity");
        }

        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:C1"));
    }

    private CellStyle getCellStyle(Workbook workbook, short dataFormat){
        DataFormat format = workbook.createDataFormat();
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(dataFormat);
        return cellStyle;
    }

}
