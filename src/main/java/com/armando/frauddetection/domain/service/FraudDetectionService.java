package com.armando.frauddetection.domain.service;

import com.armando.frauddetection.api.controller.dto.DetectFraudRequest;
import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.FraudAlertRepository;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import com.armando.frauddetection.rules.FraudRulesEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionEventRepository transactionEventRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final FraudRulesEngine fraudRulesEngine;


    public TransactionEvent buildEvent(DetectFraudRequest request) {
        return TransactionEvent.builder()
                .transactionId(request.transactionId())
                .accountId(request.accountId())
                .amount(request.amount())
                .currency(request.currency())
                .channel(request.channel())
                .ipAddress(request.ipAddress())
                .country(request.country())
                .merchantId(request.merchantId())
                .timestamp(OffsetDateTime.now())
                .flagged(Boolean.FALSE)
                .riskScore(null)
                .flagReason(null)
                .build();
    }


    public TransactionEvent saveEvent(TransactionEvent event) {
        return transactionEventRepository.save(event);
    }


    public List<FraudAlert> evaluateRules(TransactionEvent event) {
        List<FraudAlert> alerts = fraudRulesEngine.evaluate(event);

        if (!alerts.isEmpty()) {
            fraudAlertRepository.saveAll(alerts);
        }

        return alerts;
    }


    public BigDecimal calculateRiskScore(List<FraudAlert> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int score = alerts.stream()
                .mapToInt(this::scoreAlert)
                .sum();

        return BigDecimal.valueOf(score);
    }

    private int scoreAlert(FraudAlert alert) {
        String ruleCode = alert.getRuleCode();

        return switch (ruleCode) {
            case "HighAmountRule" -> 50;
            case "RiskyCountryRule" -> 60;
            case "VelocityRule"     -> 40;
            default                 -> 10;
        };
    }
}
