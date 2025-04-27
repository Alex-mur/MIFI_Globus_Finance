package it.globus.finance.model.entity;

import lombok.Getter;

@Getter
public enum Status {
    NEW("Новая"),
    CONFIRMED("Подтвержденная"),
    PROCESSING("В обработке"),
    CANCELED("Отменена"),
    PAYMENT_COMPLETED("Платеж выполнен"),
    PAYMENT_DELETED("Платеж удален"),
    REFUNDED("Возврат");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }
}
