package it.globus.finance.service;

import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class TransactionService {

    private final TransactionRepo TransactionRepo;

    public TransactionService(TransactionRepo TransactionRepo) {
        this.TransactionRepo = TransactionRepo;
    }

    public void updateTransaction(BigInteger id, TransactionUpdateRequest request) {
        Transaction transaction = TransactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setComment(request.getComment());
        transaction.setStatus(request.getStatus());

        TransactionRepo.save(transaction);
    }
}
