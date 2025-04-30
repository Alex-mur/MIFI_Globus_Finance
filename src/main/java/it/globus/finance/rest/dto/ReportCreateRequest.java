package it.globus.finance.rest.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class ReportCreateRequest {
    @Pattern(
            regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}$",
            message = "Дата должна быть в формате dd.MM.yyyy"
    )
    private String reportStartDate;

    @Pattern(
            regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}$",
            message = "Дата должна быть в формате dd.MM.yyyy"
    )
    private String reportEndDate;
}

