package it.globus.finance.model.entity;

public interface Status {
    String NEW = "Новая";
    String CONFIRMED = "Подтвержденная";
    String PROCESSING = "В обработке";
    String CANCELED = "Отменена";
    String PAYMENT_COMPLETED = "Платеж выполнен";
    String PAYMENT_DELETED = "Платеж удален";
    String REFUNDED = "Возврат";
}
