package it.globus.finance.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class TransactionCreateRequest {
    @Pattern(
            regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2}$",
            message = "Дата должна быть в формате dd.MM.yyyy HH:mm"
    )
    @NotNull(message = "Дата транзакции обязательна")
    private String transactionDate;
    @NotNull(message = "Тип транзакции обязателен")
    private String transactionType;
    @DecimalMin(value = "0.01", message = "Сумма должна быть положительной")
    @NotNull(message = "Сумма обязательна")
    private BigDecimal amount;
    private String comment;
    @NotNull(message = "Статус транзакции обязательна")
    private String status;
    private String senderBank;
    private String senderAccount;
    private String receiverBank;
    @Pattern(regexp = "^\\d{11}$", message = "ИНН должен содержать 11 цифр")
    private String receiverInn;
    private String receiverAccount;
    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Телефон должен начинаться с +7 или 8 и содержать 11 цифр")
    private String receiverPhone;
    private String receiverType;
    private Long categoryId;
}

