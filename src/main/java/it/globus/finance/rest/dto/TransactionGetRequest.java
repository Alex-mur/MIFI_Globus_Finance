package it.globus.finance.rest.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionGetRequest {
    private Long id;
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

    public TransactionGetRequest(Long id, LocalDateTime transactionDate, String transactionType, BigDecimal amount, String comment, String status, String senderBank, String senderAccount, String receiverBank, String receiverInn, String receiverAccount, String receiverPhone, String receiverType, Long categoryId) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.amount = amount;
        this.comment = comment;
        this.status = status;
        this.senderBank = senderBank;
        this.senderAccount = senderAccount;
        this.receiverBank = receiverBank;
        this.receiverInn = receiverInn;
        this.receiverAccount = receiverAccount;
        this.receiverPhone = receiverPhone;
        this.receiverType = receiverType;
        this.categoryId = categoryId;
    }
}