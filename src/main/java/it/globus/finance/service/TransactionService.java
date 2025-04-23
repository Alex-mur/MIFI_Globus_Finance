package it.globus.finance.service;

import it.globus.finance.model.entity.Category;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;

    public TransactionService(TransactionRepo transactionRepo, CategoryRepo categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    private <T> void applyIfPresent(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    public void updateTransaction(Long id, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        applyIfPresent(request.getTransactionType(), transaction::setTransactionType);
        applyIfPresent(request.getAmount(), transaction::setAmount);
        applyIfPresent(request.getComment(), transaction::setComment);
        applyIfPresent(request.getStatus(), transaction::setStatus);
        applyIfPresent(request.getSenderBank(), transaction::setSenderBank);
        applyIfPresent(request.getSenderAccount(), transaction::setSenderAccount);
        applyIfPresent(request.getReceiverBank(), transaction::setReceiverBank);
        applyIfPresent(request.getReceiverInn(), transaction::setReceiverInn);
        applyIfPresent(request.getReceiverAccount(), transaction::setReceiverAccount);
        applyIfPresent(request.getReceiverPhone(), transaction::setReceiverPhone);
        applyIfPresent(request.getReceiverType(), transaction::setReceiverType);

        applyIfPresent(request.getTransactionDate(), dateStr -> {
            LocalDateTime parsed = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            transaction.setTransactionDate(parsed);
        });

        applyIfPresent(request.getCategoryId(), catId -> {
            Category category = categoryRepo.findById(catId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            transaction.setCategory(category);
        });

        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepo.save(transaction);
    }
}
