package com.vention.agroex.controller;

import com.vention.agroex.model.ReportRequest;
import com.vention.agroex.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/baseLot")
    public ResponseEntity<Object> filterLotsByDate(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.baseLotReport(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/lotByMaxPrice")
    public ResponseEntity<Object> filterLotsByBaseAndMaxPrice(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.lotReportByMaxPrice(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/userByLotCount")
    public ResponseEntity<Object> filterUsersByLotsBaseAndMaxQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.userReportByLotCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/userByBetMoney")
    public ResponseEntity<Object> filterUserByLotsBaseAndMaxAmountInBets(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.userReportByBetMoney(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/userByLotMoney")
    public ResponseEntity<Object> filterUserByLotsBaseAndMaxAmountMoneyInLots(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.userReportByLotMoney(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByBetMoney")
    public ResponseEntity<Object> filterCountryByLotsBaseAndTotalBetAmount(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByBetMoney(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByLotCount")
    public ResponseEntity<Object> filterCountryByLotsBaseAndTotalLotQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByLotCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByOwnerCount")
    public ResponseEntity<Object> filterCountryByLotsBaseAndLotOwnersQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByOwnerCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByLotMoney")
    public ResponseEntity<byte[]> filterCountryByLotsBaseAndLotsMoneyNested(@RequestBody ReportRequest reportRequest) {
        var bytes = reportService.countryReportByLotMoney(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByParticipantCount")
    public ResponseEntity<Object> filterCountryByLotsBaseAndLotParticipantsQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByParticipantCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

}
