package it.globus.finance.service;

import it.globus.finance.configuration.exception.RequestException;
import it.globus.finance.model.entity.Category;
import it.globus.finance.model.entity.Status;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionCreateRequest;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;
    private final UserService userService;
    private static final Set<String> NON_DELETABLE_STATUSES = Set.of(
            Status.CONFIRMED.getDisplayName(),
            Status.PROCESSING.getDisplayName(),
            Status.CANCELED.getDisplayName(),
            Status.PAYMENT_COMPLETED.getDisplayName(),
            Status.REFUNDED.getDisplayName()
    );

    public TransactionService(TransactionRepo transactionRepo, CategoryRepo categoryRepo, UserService userService) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.userService = userService;
    }

    private <T> void applyIfPresent(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    public Transaction createTransaction(TransactionCreateRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUser(userService.getCurrentUser());
        transaction.setTransactionDate(LocalDateTime.now());
        applyIfPresent(request.getTransactionDate(), dateStr -> {
            LocalDateTime parsed = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            transaction.setTransactionDate(parsed);
        });
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

        applyIfPresent(request.getCategoryId(), catId -> {
            Category category = categoryRepo.findById(catId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            transaction.setCategory(category);
        });
        transactionRepo.save(transaction);
        return transaction;
    }

    public Transaction deleteTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if(!isTransactionOwnedByUser(transaction))
            throw new RequestException("Transaction is not owned by user");

        if (NON_DELETABLE_STATUSES.contains(transaction.getStatus())) {
            throw new RequestException("Cannot delete transaction with status: " + transaction.getStatus());
        }

        transaction.setStatus(String.valueOf(Status.PAYMENT_DELETED.getDisplayName()));
        transactionRepo.delete(transaction);
        return transaction;
    }

    public Transaction getTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if(!isTransactionOwnedByUser(transaction))
            throw new RequestException("Transaction is not owned by user");

        return transaction;
    }

    public Transaction updateTransaction(Long id, TransactionUpdateRequest request) {
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
        return transaction;
    }

    private boolean isTransactionOwnedByUser(Transaction transaction) {
        return userService.getCurrentUser().getId() == transaction.getUser().getId();
    }
}
