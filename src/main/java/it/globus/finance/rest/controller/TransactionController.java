package it.globus.finance.rest.controller;

import it.globus.finance.model.entity.Transaction;
import it.globus.finance.rest.dto.TransactionCreateRequest;
import it.globus.finance.rest.dto.TransactionFilterRequest;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import it.globus.finance.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createTransaction(
            @RequestBody @Valid TransactionCreateRequest request) {
        try {
            return ResponseEntity.ok(transactionService.createTransaction(request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody @Valid TransactionUpdateRequest request) {
        try {
            return ResponseEntity.ok(transactionService.updateTransaction(id, request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Transaction>> filterTransactions(@RequestBody TransactionFilterRequest request) {
        List<Transaction> filtered = transactionService.filterTransactions(request);
        return ResponseEntity.ok(filtered);
    }
}
