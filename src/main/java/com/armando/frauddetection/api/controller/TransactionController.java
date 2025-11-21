package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.api.controller.dto.TransactionEventRequest;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionEventRepository transactionEventRepository;

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
}
