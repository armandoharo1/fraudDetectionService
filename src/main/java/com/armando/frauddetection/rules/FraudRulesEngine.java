package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudRulesEngine {


    private final List<FraudRule> rules;

    public List<FraudAlert> evaluate(TransactionEvent event) {
        List<FraudAlert> alerts = new ArrayList<>();

        for (FraudRule rule : rules) {
            FraudRuleResult result = rule.apply(event);

            if (result.isTriggered()) {
                FraudAlert alert = FraudAlert.builder()
                        .transactionId(event.getTransactionId())
                        .ruleCode(rule.getClass().getSimpleName())
                        .severity(determineSeverity(rule))
                        .description(result.getReason())
                        .createdAt(OffsetDateTime.now())
                        .build();

                alerts.add(alert);
            }
        }

        return alerts;
    }

    private String determineSeverity(FraudRule rule) {
        if (rule instanceof HighAmountRule || rule instanceof RiskyCountryRule) {
            return "HIGH";
        }
        if (rule instanceof VelocityRule) {
            return "MEDIUM";
        }
        return "LOW";
    }
}
