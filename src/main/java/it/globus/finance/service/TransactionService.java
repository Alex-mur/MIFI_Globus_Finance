package it.globus.finance.service;

import it.globus.finance.model.entity.Category;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.CategoryRepo;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final CategoryRepo categoryRepo;

    public TransactionService(TransactionRepo transactionRepo, CategoryRepo categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    public void updateTransaction(Long id, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        transaction.setTransactionDate(request.getTransactionDate());
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
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            transaction.setCategory(category);
        }
        transaction.setUpdatedAt(LocalDateTime.now());

        transactionRepo.save(transaction);
    }
}
