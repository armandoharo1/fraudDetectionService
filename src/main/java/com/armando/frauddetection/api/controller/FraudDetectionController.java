package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.api.controller.dto.DetectFraudRequest;
import com.armando.frauddetection.api.controller.dto.DetectFraudResponse;
import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @Operation(summary = "Detect fraud for a transaction")
    @PostMapping("/detect")
    public ResponseEntity<DetectFraudResponse> detectFraud(@RequestBody DetectFraudRequest request) {

        // 1. Construir el evento (sin persistir aún)
        TransactionEvent event = fraudDetectionService.buildEvent(request);

        // 2. Evaluar reglas de fraude
        List<FraudAlert> alerts = fraudDetectionService.evaluateRules(event);

        // 3. Marcar el evento según las alertas + calcular riskScore
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

        // 4. Guardar el evento UNA sola vez
        TransactionEvent savedEvent = fraudDetectionService.saveEvent(event);

        // 5. Construir y devolver la respuesta
        DetectFraudResponse response = new DetectFraudResponse(savedEvent, alerts);
        return ResponseEntity.ok(response);
    }
}
