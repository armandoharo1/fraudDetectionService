package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RiskyCountryRule implements FraudRule {

    private static final Set<String> BLACKLIST = Set.of("RU", "IR", "KP", "SY");

    @Override
    public FraudRuleResult apply(TransactionEvent event) {
        if (event.getCountry() != null && BLACKLIST.contains(event.getCountry())) {
            return new FraudRuleResult(true,
                    "Transaction originated from risky country: " + event.getCountry());
        }
        return new FraudRuleResult(false, null);
    }
}
