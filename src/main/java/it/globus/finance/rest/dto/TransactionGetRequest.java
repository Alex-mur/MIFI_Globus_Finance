package it.globus.finance.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionGetRequest {
    private LocalDateTime transactionDate;
    private String transactionType;
    private BigDecimal amount;
    private String comment;
    private String status;

    // Getters and setters
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public String getStatus() {
        return status;
    }
}
