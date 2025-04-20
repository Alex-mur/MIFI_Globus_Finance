package it.globus.finance.service;

import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.repo.TransactionRepo;
import it.globus.finance.rest.dto.TransactionDto;
import it.globus.finance.rest.dto.TransactionGetRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;

    public TransactionService(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public TransactionDto getTransaction(BigInteger id, TransactionGetRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!isTransactionMatchingRequest(transaction, request)) {
            throw new EntityNotFoundException("Transaction does not match the provided criteria");
        }

        return mapToDto(transaction);
    }

    public void deleteTransaction(BigInteger id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
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

        if (request.getComment() != null && !request.getComment().equals(transaction.getComment())) {
            return false;
        }

        if (request.getStatus() != null && !request.getStatus().equals(transaction.getStatus())) {
            return false;
        }

        return true;
    }

    private TransactionDto mapToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser() != null ? transaction.getUser().getId() : null);
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setComment(transaction.getComment());
        dto.setStatus(transaction.getStatus());
        dto.setSenderBank(transaction.getSenderBank());
        dto.setSenderAccount(transaction.getSenderAccount());
        dto.setReceiverBank(transaction.getReceiverBank());
        dto.setReceiverInn(transaction.getReceiverInn());
        dto.setReceiverAccount(transaction.getReceiverAccount());
        dto.setReceiverPhone(transaction.getReceiverPhone());
        dto.setCategoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null);
        dto.setEntityType(transaction.getEntityType());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        return dto;
    }
}