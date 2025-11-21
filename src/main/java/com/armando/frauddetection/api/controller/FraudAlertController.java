package com.armando.frauddetection.api.controller;

import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.service.FraudAlertService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final FraudAlertService fraudAlertService;

    @Operation(summary = "Get fraud alerts by transactionId")
    @GetMapping
    public ResponseEntity<List<FraudAlert>> getAlerts(@RequestParam String transactionId) {
        var alerts = fraudAlertService.getAlertsByTransactionId(transactionId);
        return ResponseEntity.ok(alerts);
    }
}
