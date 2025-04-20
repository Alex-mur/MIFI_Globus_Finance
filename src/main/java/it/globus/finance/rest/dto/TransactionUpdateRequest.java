package it.globus.finance.rest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionUpdateRequest {
    private LocalDateTime transactionDate;
    private String transactionType;
    private BigDecimal amount;
    private String comment;
    private String status;
    private String senderBank;
    private String senderAccount;
    private String receiverBank;
    private String receiverInn;
    private String receiverAccount;
    private String receiverPhone;
    private String receiverType;
    private Long categoryId;
}
