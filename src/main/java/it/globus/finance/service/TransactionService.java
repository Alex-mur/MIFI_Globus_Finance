package it.globus.finance.service;

import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionDeleteRequest;
import it.globus.finance.rest.dto.TransactionGetRequest;
import jakarta.persistence.EntityNotFoundException;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;
    private static final Set<String> NON_DELETABLE_STATUSES = Set.of(
            "CONFIRMED",
            "PROCESSING",
            "CANCELED",
            "PAYMENT_COMPLETED",
            "REFUNDED"
    );

    public TransactionService(TransactionRepo transactionRepo, CategoryRepo categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    public void getTransaction(Long id, TransactionGetRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!isTransactionMatchingRequest(transaction, request)) {
            throw new EntityNotFoundException("Transaction does not match the provided criteria");
        }
    }

    public void deleteTransaction(Long id, TransactionDeleteRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (NON_DELETABLE_STATUSES.contains(transaction.getStatus().toUpperCase())) {
            throw new IllegalStateException("Cannot delete transaction with status: " + transaction.getStatus());
        }

        transactionRepo.delete(transaction);
    }

    private boolean isTransactionMatchingRequest(Transaction transaction, TransactionGetRequest request) {
        if (request == null) {
            return true;
        }

        if (request.getTransactionDate() != null && !request.getTransactionDate().equals(transaction.getTransactionDate())) {
            return false;
        }

        if (request.getTransactionType() != null && !request.getTransactionType().equals(transaction.getTransactionType())) {
            return false;
        }

        if (request.getAmount() != null && !request.getAmount().equals(transaction.getAmount())) {
            return false;
        }
        return false;
    }

    // Класс для возврата ответа с данными о транзакции и списком транзакций пользователя
    @Setter
    public static class TransactionResponse {
        private TransactionGetRequest transaction;
        private List<TransactionGetRequest> userTransactions;

        public TransactionResponse(TransactionGetRequest transaction, List<TransactionGetRequest> userTransactions) {
            this.transaction = transaction;
            this.userTransactions = userTransactions;
        }

        public void setTransaction(TransactionGetRequest transaction) { this.transaction = transaction; }

        public void setUserTransactions(List<TransactionGetRequest> userTransactions) { this.userTransactions = userTransactions; }
    }
}