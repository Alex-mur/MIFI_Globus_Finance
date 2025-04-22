package it.globus.finance.service;

import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.entity.User;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionGetRequest;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

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

    public TransactionGetRequest getTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        
        if (!isUserTransaction(transaction.getUser().getId()))
            throw new IllegalStateException("Cannot get transaction belonging to another user: only the owner can get this transaction");

        return new TransactionGetRequest(
                transaction.getId(),
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getComment(),
                transaction.getStatus(),
                transaction.getSenderBank(),
                transaction.getSenderAccount(),
                transaction.getReceiverBank(),
                transaction.getReceiverInn(),
                transaction.getReceiverAccount(),
                transaction.getReceiverPhone(),
                transaction.getReceiverType(),
                transaction.getCategory() != null ? transaction.getCategory().getId() : null
        );
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!isUserTransaction(transaction.getUser().getId())) 
            throw new IllegalStateException("Cannot delete transaction belonging to another user: only the owner can delete this transaction");
        
        if (NON_DELETABLE_STATUSES.contains(transaction.getStatus().toUpperCase())) {
            throw new IllegalStateException("Cannot delete transaction with status: " + transaction.getStatus());
        }

        transactionRepo.delete(transaction);
    }
    
    protected boolean isUserTransaction(Long id) {
        User currentUser = new User();
        return id == currentUser.getId();
    }
}