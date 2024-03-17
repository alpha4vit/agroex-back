package com.vention.agroex.controller;

import com.vention.agroex.model.ReportRequest;
import com.vention.agroex.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reports", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/baseLot")
    public ResponseEntity<byte[]> filterLotsByDate(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.baseLotReport(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/lotByMaxPrice")
    public ResponseEntity<byte[]> filterLotsByBaseAndMaxPrice(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.lotReportByMaxPrice(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/userByLotCount")
    public ResponseEntity<byte[]> filterUsersByLotsBaseAndMaxQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.userReportByLotCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/ownersByBets")
    public ResponseEntity<byte[]> filterUserByLotsBaseAndMaxAmountInBets(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.userReportOwnersByBets(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/participantsByBets")
    public ResponseEntity<byte[]> filterUserByLotsBaseAndMaxAmountMoneyInLots(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.userReportParticipantByBets(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByLotPrice")
    public ResponseEntity<byte[]> filterCountryByLotsBaseAndTotalBetAmount(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByLotPrice(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByLotCount")
    public ResponseEntity<byte[]> filterCountryByLotsBaseAndTotalLotQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByLotCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByOwnerCount")
    public ResponseEntity<byte[]> filterCountryByLotsBaseAndLotOwnersQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByOwnerCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByOwnersLotsBets")
    public ResponseEntity<byte[]> filterCountryByOwnersLotsBets(@RequestBody ReportRequest reportRequest) {
        var bytes = reportService.countryReportByOwnersLotsBets(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByParticipantCount")
    public ResponseEntity<Object> filterCountryByLotsBaseAndLotParticipantsQuantity(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByParticipantCount(reportRequest);
        return ResponseEntity.ok(bytes);
    }

    @PostMapping("/countryByParticipantBets")
    public ResponseEntity<Object> filterCountryByLotsBaseAndLotParticipantBets(@RequestBody ReportRequest reportRequest){
        var bytes = reportService.countryReportByParticipantBets(reportRequest);
        return ResponseEntity.ok(bytes);
    }

}
