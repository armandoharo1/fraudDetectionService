package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.repository.FraudAlertRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final FraudAlertRepository fraudAlertRepository;

    @Operation(summary = "List all fraud alerts (paginated)")
    @GetMapping
    public ResponseEntity<Page<FraudAlert>> listAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<FraudAlert> result = fraudAlertRepository.findAll(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "List fraud alerts by transactionId (paginated)")
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Page<FraudAlert>> listByTransaction(
            @PathVariable String transactionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FraudAlert> result =
                fraudAlertRepository.findAllByTransactionId(transactionId, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "List fraud alerts by severity (paginated)")
    @GetMapping("/severity/{severity}")
    public ResponseEntity<Page<FraudAlert>> listBySeverity(
            @PathVariable String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FraudAlert> result =
                fraudAlertRepository.findAllBySeverity(severity.toUpperCase(), pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get fraud alert by id")
    @GetMapping("/{id}")
    public ResponseEntity<FraudAlert> getById(@PathVariable Long id) {
        return fraudAlertRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
