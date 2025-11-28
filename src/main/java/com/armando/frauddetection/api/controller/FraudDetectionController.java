package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.api.controller.dto.DetectFraudRequest;
import com.armando.frauddetection.api.controller.dto.DetectFraudResponse;
import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ROLE_ANALYST')")
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @Operation(summary = "Detect fraud for a transaction")
    @PostMapping("/detect")
    public ResponseEntity<DetectFraudResponse> detectFraud(
            @Valid @RequestBody DetectFraudRequest request
    ) {

        TransactionEvent event = fraudDetectionService.buildEvent(request);

        List<FraudAlert> alerts = fraudDetectionService.evaluateRules(event);

        if (!alerts.isEmpty()) {
            event.setFlagged(Boolean.TRUE);

            String reasons = alerts.stream()
                    .map(FraudAlert::getDescription)
                    .reduce((a, b) -> a + " | " + b)
                    .orElse("");

            event.setFlagReason(reasons);
            event.setRiskScore(fraudDetectionService.calculateRiskScore(alerts));
        } else {
            event.setFlagged(Boolean.FALSE);
            event.setFlagReason(null);
            event.setRiskScore(java.math.BigDecimal.ZERO);
        }

        TransactionEvent savedEvent = fraudDetectionService.saveEvent(event);

        DetectFraudResponse response = new DetectFraudResponse(savedEvent, alerts);
        return ResponseEntity.ok(response);
    }
}
