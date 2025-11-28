package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HighAmountRule implements FraudRule {

    private static final BigDecimal THRESHOLD = BigDecimal.valueOf(2000.00);

    @Override
    public FraudRuleResult apply(TransactionEvent event) {
        if (event.getAmount() != null
                && event.getAmount().compareTo(THRESHOLD) > 0) {

            return new FraudRuleResult(
                    true,
                    "High transaction amount detected: " + event.getAmount()
            );
        }

        return new FraudRuleResult(false, null);
    }
}
