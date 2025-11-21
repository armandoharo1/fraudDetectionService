package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;

public interface FraudRule {

    FraudRuleResult apply(TransactionEvent event);
}
