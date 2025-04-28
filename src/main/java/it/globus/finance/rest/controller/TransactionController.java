package it.globus.finance.rest.controller;

import it.globus.finance.rest.dto.TransactionCreateRequest;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import it.globus.finance.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(transactionService.getTransaction(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(transactionService.deleteTransaction(id));
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
}
