package it.globus.finance.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    @Column(nullable = false, columnDefinition = "NUMERIC(19,5)")
    private BigDecimal amount;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "sender_bank", length = 100)
    private String senderBank;

    @Column(name = "sender_account", length = 50)
    private String senderAccount;

    @Column(name = "receiver_bank", length = 100)
    private String receiverBank;

    @Column(name = "receiver_inn", length = 12)
    private String receiverInn;

    @Column(name = "receiver_account", length = 50)
    private String receiverAccount;

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "entity_type", length = 50)
    private String receiverType;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Transaction() {
    }

    public Transaction(User user, LocalDateTime transactionDate, String transactionType, BigDecimal amount, String comment, String status, String senderBank, String senderAccount, String receiverBank, String receiverInn, String receiverAccount, String receiverPhone, Category category, String receiverType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
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
        this.category = category;
        this.receiverType = receiverType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
