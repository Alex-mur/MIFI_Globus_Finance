package it.globus.finance.rest.controller;

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
            @PathVariable BigInteger id,
            @RequestBody TransactionUpdateRequest request) {
        try {
            transactionService.updateTransaction(id, request);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
