package it.globus.finance.rest.controller;

import it.globus.finance.rest.dto.TransactionDto;
import it.globus.finance.rest.dto.TransactionGetRequest;
import it.globus.finance.rest.dto.TransactionUpdateRequest;
import it.globus.finance.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionUpdateRequest request) {
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(
            @PathVariable BigInteger id,
            TransactionGetRequest request) {
        try {
            transactionService.updateTransaction(id, request);
            TransactionDto transactionDto = transactionService.getTransaction(id, request);
            return ResponseEntity.ok(transactionDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable BigInteger id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}