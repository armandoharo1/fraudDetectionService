package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class HighAmountRuleTest {

    private final HighAmountRule rule = new HighAmountRule();

    @Test
    void apply_shouldTriggerWhenAmountIsAboveThreshold() {
        TransactionEvent event = TransactionEvent.builder()
                .amount(BigDecimal.valueOf(3000.00)) // > 2000
                .build();

        FraudRuleResult result = rule.apply(event);

        assertTrue(result.isTriggered());
        assertNotNull(result.getReason());
        assertTrue(result.getReason().contains("3000"),
                "Reason should mention the transaction amount");
    }

    @Test
    void apply_shouldNotTriggerWhenAmountIsBelowOrEqualThreshold() {
        TransactionEvent eventBelow = TransactionEvent.builder()
                .amount(BigDecimal.valueOf(1500.00))
                .build();

        TransactionEvent eventEqual = TransactionEvent.builder()
                .amount(BigDecimal.valueOf(2000.00)) // equal to threshold
                .build();

        TransactionEvent eventNull = TransactionEvent.builder()
                .amount(null)
                .build();

        FraudRuleResult resultBelow = rule.apply(eventBelow);
        FraudRuleResult resultEqual = rule.apply(eventEqual);
        FraudRuleResult resultNull = rule.apply(eventNull);

        assertFalse(resultBelow.isTriggered());
        assertNull(resultBelow.getReason());

        assertFalse(resultEqual.isTriggered());
        assertNull(resultEqual.getReason());

        assertFalse(resultNull.isTriggered());
        assertNull(resultNull.getReason());
    }
}
