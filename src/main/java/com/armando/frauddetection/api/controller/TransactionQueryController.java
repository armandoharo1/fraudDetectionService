package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@PreAuthorize("hasAnyRole('ROLE_ANALYST','ROLE_AUDITOR')")
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionQueryController {

    private final TransactionEventRepository transactionEventRepository;

    @Operation(summary = "List all transactions with optional filters (paginated)")
    @GetMapping
    public ResponseEntity<Page<TransactionEvent>> listTransactions(
            @RequestParam(required = false) Boolean flagged,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) BigDecimal minRiskScore,
            Pageable pageable
    ) {
        Page<TransactionEvent> page;

        if (flagged != null) {
            page = transactionEventRepository.findAllByFlagged(flagged, pageable);
        } else if (accountId != null) {
            page = transactionEventRepository.findAllByAccountId(accountId, pageable);
        } else if (minRiskScore != null) {
            page = transactionEventRepository.findAllByRiskScoreGreaterThanEqual(minRiskScore, pageable);
        } else {
            page = transactionEventRepository.findAll(pageable);
        }

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "List high-risk transactions (paginated)")
    @GetMapping("/high-risk")
    public ResponseEntity<Page<TransactionEvent>> listHighRiskTransactions(
            @RequestParam(defaultValue = "80") BigDecimal minRiskScore,
            Pageable pageable
    ) {
        Page<TransactionEvent> page =
                transactionEventRepository.findAllByRiskScoreGreaterThanEqual(minRiskScore, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "List transactions for a given account (paginated)")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionEvent>> listByAccount(
            @PathVariable String accountId,
            Pageable pageable
    ) {
        Page<TransactionEvent> page =
                transactionEventRepository.findAllByAccountId(accountId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get transaction by id")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionEvent> getById(@PathVariable Long id) {
        return transactionEventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
