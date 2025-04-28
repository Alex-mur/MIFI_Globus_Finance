package it.globus.finance.service;

import it.globus.finance.configuration.exception.RequestException;
import it.globus.finance.model.entity.Category;
import it.globus.finance.model.entity.Status;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionCreateRequest;
import it.globus.finance.rest.dto.TransactionFilterRequest;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;
    private final UserService userService;
    private final EntityManager entityManager;
    private static final Set<String> NON_DELETABLE_STATUSES = Set.of(
            Status.CONFIRMED.getDisplayName(),
            Status.PROCESSING.getDisplayName(),
            Status.CANCELED.getDisplayName(),
            Status.PAYMENT_COMPLETED.getDisplayName(),
            Status.REFUNDED.getDisplayName()
    );

    public TransactionService(TransactionRepo transactionRepo, CategoryRepo categoryRepo, UserService userService, EntityManager entityManager) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.userService = userService;
        this.entityManager = entityManager;
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
        fillTransactionData(transaction, request.getTransactionType(), request.getAmount(),
                request.getComment(), request.getStatus(), request.getSenderBank(),
                request.getSenderAccount(), request.getReceiverBank(), request.getReceiverInn(),
                request.getReceiverAccount(), request.getReceiverPhone(), request.getReceiverType());

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

        fillTransactionData(transaction, request.getTransactionType(), request.getAmount(),
                request.getComment(), request.getStatus(), request.getSenderBank(),
                request.getSenderAccount(), request.getReceiverBank(), request.getReceiverInn(),
                request.getReceiverAccount(), request.getReceiverPhone(), request.getReceiverType());

        applyIfPresent(request.getTransactionDate(), dateStr -> {
            LocalDateTime parsed = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            transaction.setTransactionDate(parsed);
        });

        applyIfPresent(request.getCategoryId(), catId -> {
            Category category = categoryRepo.findById(catId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            transaction.setCategory(category);
        });
        transactionRepo.save(transaction);
        return transaction;
    }

    @Transactional(readOnly = true)
    public List<Transaction> filterTransactions(TransactionFilterRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = query.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        applyIfPresent(request.getSenderBank(), senderBank ->
                predicates.add(cb.equal(transaction.get("senderBank"), senderBank))
        );

        applyIfPresent(request.getReceiverBank(), receiverBank ->
                predicates.add(cb.equal(transaction.get("receiverBank"), receiverBank))
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        applyIfPresent(request.getDateExact(), dateStr -> {
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
            predicates.add(cb.equal(transaction.get("transactionDate"), dateTime));
        });

        applyIfPresent(request.getDateFrom(), dateStr -> {
            LocalDateTime dateTimeFrom = LocalDateTime.parse(dateStr, formatter);
            predicates.add(cb.greaterThanOrEqualTo(transaction.get("transactionDate"), dateTimeFrom));
        });

        applyIfPresent(request.getDateTo(), dateStr -> {
            LocalDateTime dateTimeTo = LocalDateTime.parse(dateStr, formatter);
            predicates.add(cb.lessThanOrEqualTo(transaction.get("transactionDate"), dateTimeTo));
        });

        applyIfPresent(request.getStatus(), status ->
                predicates.add(cb.equal(transaction.get("status"), status))
        );

        applyIfPresent(request.getReceiverInn(), receiverInn ->
                predicates.add(cb.equal(transaction.get("receiverInn"), receiverInn))
        );

        applyIfPresent(request.getAmountFrom(), amountFrom ->
                predicates.add(cb.greaterThanOrEqualTo(transaction.get("amount"), amountFrom))
        );

        applyIfPresent(request.getAmountTo(), amountTo ->
                predicates.add(cb.lessThanOrEqualTo(transaction.get("amount"), amountTo))
        );

        applyIfPresent(request.getTransactionType(), transactionType ->
                predicates.add(cb.equal(transaction.get("transactionType"), transactionType))
        );

        applyIfPresent(request.getCategoryId(), categoryId ->
                predicates.add(cb.equal(transaction.get("category").get("id"), categoryId))
        );

        query.select(transaction).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

    private boolean isTransactionOwnedByUser(Transaction transaction) {
        return userService.getCurrentUser().getId() == transaction.getUser().getId();
    }

    private void fillTransactionData(Transaction transaction, String transactionType, BigDecimal amount, String comment,
                                     String status, String senderBank, String senderAccount, String receiverBank,
                                     String receiverInn, String receiverAccount, String receiverPhone,
                                     String receiverType) {
        applyIfPresent(transactionType, transaction::setTransactionType);
        applyIfPresent(amount, transaction::setAmount);
        applyIfPresent(comment, transaction::setComment);
        applyIfPresent(status, transaction::setStatus);
        applyIfPresent(senderBank, transaction::setSenderBank);
        applyIfPresent(senderAccount, transaction::setSenderAccount);
        applyIfPresent(receiverBank, transaction::setReceiverBank);
        applyIfPresent(receiverInn, transaction::setReceiverInn);
        applyIfPresent(receiverAccount, transaction::setReceiverAccount);
        applyIfPresent(receiverPhone, transaction::setReceiverPhone);
        applyIfPresent(receiverType, transaction::setReceiverType);
    }
}
