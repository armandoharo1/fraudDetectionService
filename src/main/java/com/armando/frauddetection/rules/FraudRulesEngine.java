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
                        // usamos el nombre de la clase como código de regla (ej: HighAmountRule)
                        .ruleCode(rule.getClass().getSimpleName())
                        // por ahora dejamos severidad fija; luego podemos sacarla de la regla
                        .severity("HIGH")
                        .description(result.getReason())
                        .createdAt(OffsetDateTime.now())
                        .build();

                alerts.add(alert);
            }
        }

        return alerts;
    }
}
