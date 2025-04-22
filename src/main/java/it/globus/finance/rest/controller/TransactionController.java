package it.globus.finance.rest.controller;

import it.globus.finance.rest.dto.TransactionGetRequest;
import it.globus.finance.rest.dto.TransactionDeleteRequest;
import it.globus.finance.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> getTransaction(
            @PathVariable Long id,
            @RequestBody TransactionGetRequest request) {
        try {
            transactionService.getTransaction(id, request);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @RequestBody TransactionDeleteRequest request) {
        try {
            transactionService.deleteTransaction(id, request);

            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}