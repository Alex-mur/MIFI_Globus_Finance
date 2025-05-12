package it.globus.finance.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionUpdateRequest {
    @Pattern(
            regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2}$",
            message = "Дата должна быть в формате dd.MM.yyyy HH:mm"
    )
    private String transactionDate;
    private String transactionType;
    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть положительной")
    private BigDecimal amount;
    private String comment;
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
