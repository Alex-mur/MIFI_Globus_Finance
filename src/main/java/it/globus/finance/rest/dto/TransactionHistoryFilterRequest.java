package it.globus.finance.rest.dto;

import lombok.Data;

@Data
public class TransactionHistoryFilterRequest {
    private String dateFrom; // "dd.MM.yyyy HH:mm"
    private String dateTo;   // "dd.MM.yyyy HH:mm"
}
