package it.globus.finance.service;

import it.globus.finance.configuration.exception.RequestException;
import it.globus.finance.model.entity.*;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionHistoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionCreateRequest;
import it.globus.finance.rest.dto.TransactionFilterRequest;
import it.globus.finance.rest.dto.TransactionHistoryFilterRequest;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;
    private final TransactionHistoryRepo transactionHistoryRepo;
    private final UserService userService;

    @PersistenceContext
    private final EntityManager entityManager;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final List<String> notModifiable = List.of(
            Status.CONFIRMED,
            Status.CANCELED,
            Status.PROCESSING,
            Status.PAYMENT_COMPLETED,
            Status.REFUNDED,
            Status.PAYMENT_DELETED
    );

    public TransactionService(TransactionRepo transactionRepo,
                              CategoryRepo categoryRepo,
                              TransactionHistoryRepo transactionHistoryRepo,
                              UserService userService,
                              EntityManager entityManager) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.transactionHistoryRepo = transactionHistoryRepo;
        this.userService = userService;
        this.entityManager = entityManager;
    }

    @Transactional
    public Transaction createTransaction(TransactionCreateRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUser(userService.getCurrentUser());
        fillTransactionDataOnCreate(transaction, request);
        transactionRepo.save(transaction);

        saveCreateHistory(transaction);
        return transaction;
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new RequestException("Транзакция не найдена"));

        checkUserAccess(transaction);

        if (notModifiable.contains(transaction.getStatus()))
            throw new RequestException("Обновление транзакции запрещено для статуса: " + transaction.getStatus());

        fillTransactionDataOnUpdate(transaction, request);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepo.save(transaction);
        return transaction;
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new RequestException("Транзакция не найдена"));

        checkUserAccess(transaction);

        if (notModifiable.contains(transaction.getStatus()))
            throw new RequestException("Удаление транзакции запрещено для статуса: " + transaction.getStatus());

        saveDeleteHistory(transaction);

        transaction.setStatus(Status.PAYMENT_DELETED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepo.save(transaction);
    }


    @Transactional(readOnly = true)
    public Transaction getTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new RequestException("Транзакция не найдена"));

        checkUserAccess(transaction);

        if (Objects.equals(transaction.getStatus(), Status.PAYMENT_DELETED))
            throw new RequestException("Транзакция была удалена");

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

        applyIfPresent(request.getDateExact(), dateStr -> {
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
            predicates.add(cb.equal(transaction.get("transactionDate"), dateTime));
        });

        applyIfPresent(request.getDateFrom(), dateStr -> {
            LocalDateTime dateFrom = LocalDateTime.parse(dateStr, formatter);
            predicates.add(cb.greaterThanOrEqualTo(transaction.get("transactionDate"), dateFrom));
        });

        applyIfPresent(request.getDateTo(), dateStr -> {
            LocalDateTime dateTo = LocalDateTime.parse(dateStr, formatter);
            predicates.add(cb.lessThanOrEqualTo(transaction.get("transactionDate"), dateTo));
        });

        applyIfPresent(request.getStatus(), status ->
                predicates.add(cb.equal(transaction.get("status"), status))
        );

        applyIfPresent(request.getReceiverInn(), inn ->
                predicates.add(cb.equal(transaction.get("receiverInn"), inn))
        );

        applyIfPresent(request.getAmountFrom(), amountFrom ->
                predicates.add(cb.greaterThanOrEqualTo(transaction.get("amount"), amountFrom))
        );

        applyIfPresent(request.getAmountTo(), amountTo ->
                predicates.add(cb.lessThanOrEqualTo(transaction.get("amount"), amountTo))
        );

        applyIfPresent(request.getTransactionType(), type ->
                predicates.add(cb.equal(transaction.get("transactionType"), type))
        );

        applyIfPresent(request.getCategoryId(), categoryId ->
                predicates.add(cb.equal(transaction.get("category").get("id"), categoryId))
        );
        predicates.add(cb.notEqual(transaction.get("status"), Status.PAYMENT_DELETED));
        query.select(transaction).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public List<TransactionHistory> getTransactionHistory(TransactionHistoryFilterRequest request) {
        Long userId = userService.getCurrentUser().getId();

        if (request.getDateFrom() != null && request.getDateTo() != null) {
            LocalDateTime start = LocalDateTime.parse(request.getDateFrom(), formatter);
            LocalDateTime end = LocalDateTime.parse(request.getDateTo(), formatter);
            return transactionHistoryRepo.findAllByUserIdAndChangeDateBetween(userId, start, end);
        } else {
            return transactionHistoryRepo.findAllByUserId(userId);
        }
    }

    private <T> void applyIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private void fillTransactionDataOnCreate(Transaction transaction, TransactionCreateRequest request) {
        transaction.setTransactionDate(LocalDateTime.parse(request.getTransactionDate(), formatter));
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setComment(request.getComment());
        transaction.setStatus(request.getStatus());
        transaction.setSenderBank(request.getSenderBank());
        transaction.setSenderAccount(request.getSenderAccount());
        transaction.setReceiverBank(request.getReceiverBank());
        transaction.setReceiverInn(request.getReceiverInn());
        transaction.setReceiverAccount(request.getReceiverAccount());
        transaction.setReceiverPhone(request.getReceiverPhone());
        transaction.setReceiverType(request.getReceiverType());

        if (request.getCategoryId() != null) {
            Category category = categoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new RequestException("Категория не найдена"));
            transaction.setCategory(category);
        }
    }

    private void fillTransactionDataOnUpdate(Transaction transaction, TransactionUpdateRequest request) {
        applyIfPresent(request.getTransactionDate(), dateStr -> {
            LocalDateTime parsedDate = LocalDateTime.parse(dateStr, formatter);
            saveFieldChange(transaction, "transactionDate", transaction.getTransactionDate(), parsedDate);
            transaction.setTransactionDate(parsedDate);
        });

        applyIfPresent(request.getTransactionType(), type -> {
            saveFieldChange(transaction, "transactionType", transaction.getTransactionType(), type);
            transaction.setTransactionType(type);
        });

        applyIfPresent(request.getAmount(), amount -> {
            saveFieldChange(transaction, "amount", transaction.getAmount(), amount);
            transaction.setAmount(amount);
        });

        applyIfPresent(request.getComment(), comment -> {
            saveFieldChange(transaction, "comment", transaction.getComment(), comment);
            transaction.setComment(comment);
        });

        applyIfPresent(request.getStatus(), status -> {
            saveFieldChange(transaction, "status", transaction.getStatus(), status);
            transaction.setStatus(status);
        });

        applyIfPresent(request.getSenderBank(), bank -> {
            saveFieldChange(transaction, "senderBank", transaction.getSenderBank(), bank);
            transaction.setSenderBank(bank);
        });

        applyIfPresent(request.getSenderAccount(), acc -> {
            saveFieldChange(transaction, "senderAccount", transaction.getSenderAccount(), acc);
            transaction.setSenderAccount(acc);
        });

        applyIfPresent(request.getReceiverBank(), bank -> {
            saveFieldChange(transaction, "receiverBank", transaction.getReceiverBank(), bank);
            transaction.setReceiverBank(bank);
        });

        applyIfPresent(request.getReceiverInn(), inn -> {
            saveFieldChange(transaction, "receiverInn", transaction.getReceiverInn(), inn);
            transaction.setReceiverInn(inn);
        });

        applyIfPresent(request.getReceiverAccount(), acc -> {
            saveFieldChange(transaction, "receiverAccount", transaction.getReceiverAccount(), acc);
            transaction.setReceiverAccount(acc);
        });

        applyIfPresent(request.getReceiverPhone(), phone -> {
            saveFieldChange(transaction, "receiverPhone", transaction.getReceiverPhone(), phone);
            transaction.setReceiverPhone(phone);
        });

        applyIfPresent(request.getReceiverType(), type -> {
            saveFieldChange(transaction, "receiverType", transaction.getReceiverType(), type);
            transaction.setReceiverType(type);
        });

        applyIfPresent(request.getCategoryId(), categoryId -> {
            Category newCategory = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new RequestException("Категория не найдена"));
            saveFieldChange(transaction, "category",
                    transaction.getCategory() == null ? null : transaction.getCategory().getId(),
                    newCategory.getId());
            transaction.setCategory(newCategory);
        });
    }

    private void saveFieldChange(Transaction transaction, String fieldName, Object oldValue, Object newValue) {
        if ((oldValue == null && newValue == null) || (oldValue != null && oldValue.equals(newValue))) {
            return;
        }

        TransactionHistory history = new TransactionHistory();
        history.setUser(transaction.getUser());
        history.setTransaction(transaction);
        history.setFieldName(fieldName);
        history.setOldValue(oldValue == null ? "null" : oldValue.toString());
        history.setNewValue(newValue == null ? "null" : newValue.toString());
        history.setChangeDate(LocalDateTime.now());

        transactionHistoryRepo.save(history);
    }

    private void saveCreateHistory(Transaction transaction) {
        TransactionHistory history = new TransactionHistory();
        history.setUser(transaction.getUser());
        history.setTransaction(transaction);
        history.setFieldName("CREATE");
        history.setOldValue("null");
        history.setNewValue("Создана транзакция");
        history.setChangeDate(LocalDateTime.now());

        transactionHistoryRepo.save(history);
    }

    private void saveDeleteHistory(Transaction transaction) {
        TransactionHistory history = new TransactionHistory();
        history.setUser(transaction.getUser());
        history.setTransaction(transaction);
        history.setFieldName("DELETE");
        history.setOldValue("Существующая транзакция");
        history.setNewValue("Удалена транзакция");
        history.setChangeDate(LocalDateTime.now());

        transactionHistoryRepo.save(history);
    }

    private void checkUserAccess(Transaction transaction) {
        User user = userService.getCurrentUser();
        if (transaction.getUser().getId() != user.getId()) {
            throw new RequestException("Нет доступа к данной транзакции");
        }
    }
}
