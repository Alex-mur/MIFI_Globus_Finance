package it.globus.finance.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionFilterRequest {
    private String senderBank;
    private String receiverBank;
    private String dateFrom;
    private String dateTo;
    private String dateExact;
    private String status;
    private String receiverInn;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private String transactionType;
    private Long categoryId;
}
