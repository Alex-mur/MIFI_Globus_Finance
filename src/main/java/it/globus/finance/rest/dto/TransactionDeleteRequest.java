package it.globus.finance.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionDeleteRequest {
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
