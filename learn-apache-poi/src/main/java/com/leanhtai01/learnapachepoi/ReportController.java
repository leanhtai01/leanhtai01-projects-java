package com.leanhtai01.learnapachepoi;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/xlsx")
    public ResponseEntity<byte[]> generateXlsxReport() throws Exception {
        byte[] report = reportService.generateXlsxReport();
        return createResponseEntity(report, "report.xlsx");
    }

    @PostMapping("/xls")
    public ResponseEntity<byte[]> generateXlsReport() throws Exception {
        byte[] report = reportService.generateXlsReport();
        return createResponseEntity(report, "report.xls");
    }

    private ResponseEntity<byte[]> createResponseEntity(byte[] report, String fileName) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(report);
    }
}
