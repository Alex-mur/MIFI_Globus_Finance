package it.globus.finance.rest.controller;

import com.lowagie.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.globus.finance.rest.dto.FileSaveResponse;
import it.globus.finance.rest.dto.ReportCreateRequest;
import it.globus.finance.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Создание PDF-отчета")
    @PostMapping("/generate")
    public ResponseEntity<FileSaveResponse> createReport(
            @RequestBody @Valid ReportCreateRequest request
    ) {
        try {
            String filename = reportService.generateReport(request);
            URI downloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/reports/")
                    .path(filename)
                    .path("/download")
                    .build()
                    .toUri();
            return ResponseEntity.created(downloadUri).body(
                    new FileSaveResponse(filename, downloadUri.toString()));
        } catch (IOException | DocumentException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Загрузка PDF-отчета")
    @GetMapping("/{filename}/download")
    public ResponseEntity<byte[]> getReport(@PathVariable String filename) {
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportService.getReport(filename));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalAccessError e) {
            return ResponseEntity.status(403).build();
        }
    }
}
