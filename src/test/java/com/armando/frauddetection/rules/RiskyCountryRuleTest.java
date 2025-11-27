package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskyCountryRuleTest {

    private final RiskyCountryRule rule = new RiskyCountryRule();

    @Test
    void apply_shouldTriggerWhenCountryIsBlacklisted() {
        // given
        TransactionEvent event = TransactionEvent.builder()
                .country("RU") // está en el BLACKLIST
                .build();

        // when
        FraudRuleResult result = rule.apply(event);

        // then
        assertTrue(result.isTriggered());
        assertNotNull(result.getReason());
        assertTrue(result.getReason().contains("RU"));
    }

    @Test
    void apply_shouldNotTriggerWhenCountryIsSafeOrNull() {
        // given
        TransactionEvent safeCountry = TransactionEvent.builder()
                .country("PE")
                .build();

        TransactionEvent nullCountry = TransactionEvent.builder()
                .country(null)
                .build();

        // when
        FraudRuleResult resultSafe = rule.apply(safeCountry);
        FraudRuleResult resultNull = rule.apply(nullCountry);

        // then
        assertFalse(resultSafe.isTriggered());
        assertNull(resultSafe.getReason());

        assertFalse(resultNull.isTriggered());
        assertNull(resultNull.getReason());
    }
}
