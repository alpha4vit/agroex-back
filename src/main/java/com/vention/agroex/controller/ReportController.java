package com.vention.agroex.controller;

import com.vention.agroex.model.ReportRequest;
import com.vention.agroex.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reports")
public class ReportController {

    private final ReportService reportService;
    private final HttpHeaders headers = new HttpHeaders();

    {
        headers.add("Content-Disposition", "attachment; filename=file.xlsx");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
    }

    @SneakyThrows
    @PostMapping("/baseLot")
    public ResponseEntity<InputStreamResource> filterLotsByDate(@RequestBody ReportRequest reportRequest){
        var resource = reportService.baseLotReport(reportRequest);
        return ResponseEntity
                .ok()
                .contentLength(resource.contentLength())
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/lotByMaxPrice")
    public ResponseEntity<InputStreamResource> filterLotsByBaseAndMaxPrice(@RequestBody ReportRequest reportRequest){
        var resource = reportService.lotReportByMaxPrice(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/userByLotCount")
    public ResponseEntity<InputStreamResource> filterUsersByLotsBaseAndMaxQuantity(@RequestBody ReportRequest reportRequest){
        var resource = reportService.userReportByLotCount(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/ownersByBets")
    public ResponseEntity<InputStreamResource> filterUserByLotsBaseAndMaxAmountInBets(@RequestBody ReportRequest reportRequest){
        var resource = reportService.userReportOwnersByBets(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/participantsByBets")
    public ResponseEntity<InputStreamResource> filterUserByLotsBaseAndMaxAmountMoneyInLots(@RequestBody ReportRequest reportRequest){
        var resource = reportService.userReportParticipantByBets(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/countryByLotPrice")
    public ResponseEntity<InputStreamResource> filterCountryByLotsBaseAndTotalBetAmount(@RequestBody ReportRequest reportRequest){
        var resource = reportService.countryReportByLotPrice(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/countryByLotCount")
    public ResponseEntity<InputStreamResource> filterCountryByLotsBaseAndTotalLotQuantity(@RequestBody ReportRequest reportRequest){
        var resource = reportService.countryReportByLotCount(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/countryByOwnerCount")
    public ResponseEntity<InputStreamResource> filterCountryByLotsBaseAndLotOwnersQuantity(@RequestBody ReportRequest reportRequest){
        var resource = reportService.countryReportByOwnerCount(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/countryByOwnersLotsBets")
    public ResponseEntity<InputStreamResource> filterCountryByOwnersLotsBets(@RequestBody ReportRequest reportRequest) {
        var resource = reportService.countryReportByOwnersLotsBets(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/countryByParticipantCount")
    public ResponseEntity<InputStreamResource> filterCountryByLotsBaseAndLotParticipantsQuantity(@RequestBody ReportRequest reportRequest){
        var resource = reportService.countryReportByParticipantCount(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @SneakyThrows
    @PostMapping("/countryByParticipantBets")
    public ResponseEntity<InputStreamResource> filterCountryByLotsBaseAndLotParticipantBets(@RequestBody ReportRequest reportRequest){
        var resource = reportService.countryReportByParticipantBets(reportRequest);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(resource.getInputStream()));
    }

}
