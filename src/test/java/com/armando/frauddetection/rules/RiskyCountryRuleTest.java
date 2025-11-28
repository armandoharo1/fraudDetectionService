package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskyCountryRuleTest {

    private final RiskyCountryRule rule = new RiskyCountryRule();

    @Test
    void apply_shouldTriggerWhenCountryIsBlacklisted() {
        TransactionEvent event = TransactionEvent.builder()
                .country("RU") // is in the BLACKLIST
                .build();

        FraudRuleResult result = rule.apply(event);

        assertTrue(result.isTriggered());
        assertNotNull(result.getReason());
        assertTrue(result.getReason().contains("RU"));
    }

    @Test
    void apply_shouldNotTriggerWhenCountryIsSafeOrNull() {
        TransactionEvent safeCountry = TransactionEvent.builder()
                .country("PE")
                .build();

        TransactionEvent nullCountry = TransactionEvent.builder()
                .country(null)
                .build();

        FraudRuleResult resultSafe = rule.apply(safeCountry);
        FraudRuleResult resultNull = rule.apply(nullCountry);

        assertFalse(resultSafe.isTriggered());
        assertNull(resultSafe.getReason());

        assertFalse(resultNull.isTriggered());
        assertNull(resultNull.getReason());
    }
}
