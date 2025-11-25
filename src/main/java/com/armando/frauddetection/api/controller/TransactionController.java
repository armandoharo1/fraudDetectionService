package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.api.controller.dto.TransactionEventRequest;
import com.armando.frauddetection.api.controller.dto.TransactionWithAlertsResponse;
import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import com.armando.frauddetection.domain.service.FraudAlertService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@PreAuthorize("hasRole('ROLE_ANALYST')")
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionEventRepository transactionEventRepository;
    private final FraudAlertService fraudAlertService;

    @Operation(summary = "Create a transaction event")
    @PostMapping
    public ResponseEntity<TransactionEvent> createTransaction(@RequestBody TransactionEventRequest request) {

        TransactionEvent event = TransactionEvent.builder()
                .transactionId(request.transactionId())
                .accountId(request.accountId())
                .amount(request.amount())
                .currency(request.currency())
                .channel(request.channel())
                .ipAddress(request.ipAddress())
                .country(request.country())
                .merchantId(request.merchantId())
                .timestamp(OffsetDateTime.now())
                .flagged(false)
                .riskScore(null)
                .flagReason(null)
                .build();

        TransactionEvent saved = transactionEventRepository.save(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Create a transaction and evaluate fraud rules")
    @PostMapping("/evaluate")
    public ResponseEntity<TransactionWithAlertsResponse> createAndEvaluate(
            @RequestBody TransactionEventRequest request) {

        // 1. Persistimos la transacción
        TransactionEvent event = TransactionEvent.builder()
                .transactionId(request.transactionId())
                .accountId(request.accountId())
                .amount(request.amount())
                .currency(request.currency())
                .channel(request.channel())
                .ipAddress(request.ipAddress())
                .country(request.country())
                .merchantId(request.merchantId())
                .timestamp(OffsetDateTime.now())
                .flagged(false)
                .riskScore(null)
                .flagReason(null)
                .build();

        TransactionEvent saved = transactionEventRepository.save(event);

        // 2. Ejecutamos el motor de reglas y persistimos alertas
        List<FraudAlert> alerts = fraudAlertService.evaluateAndPersist(saved);

        // 3. Construimos respuesta combinada
        TransactionWithAlertsResponse response =
                new TransactionWithAlertsResponse(saved, alerts);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
