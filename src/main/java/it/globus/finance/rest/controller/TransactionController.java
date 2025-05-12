package it.globus.finance.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import it.globus.finance.model.entity.Transaction;
import it.globus.finance.model.entity.TransactionHistory;
import it.globus.finance.rest.dto.TransactionCreateRequest;
import it.globus.finance.rest.dto.TransactionFilterRequest;
import it.globus.finance.rest.dto.TransactionHistoryFilterRequest;
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


    @Operation(summary = "Создание новой транзакции")
    @PostMapping()
    public ResponseEntity<?> createTransaction(
            @RequestBody @Valid TransactionCreateRequest request) {
        try {
            return ResponseEntity.ok(transactionService.createTransaction(request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получение транзакции по ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(transactionService.getTransaction(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удаление транзакции по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Обновление транзакции по ID")
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

    @Operation(summary = "Фильтрация транзакций")
    @PostMapping("/filter")
    public ResponseEntity<List<Transaction>> filterTransactions(@RequestBody TransactionFilterRequest request) {
        List<Transaction> filtered = transactionService.filterTransactions(request);
        return ResponseEntity.ok(filtered);
    }

    @Operation(summary = "История всех транзакций или в диапазоне дат")
    @PostMapping("/history")
    public ResponseEntity<List<TransactionHistory>> getTransactionHistory(
            @RequestBody @Valid TransactionHistoryFilterRequest request) {
        List<TransactionHistory> history = transactionService.getTransactionHistory(request);
        return ResponseEntity.ok(history);
    }

}
