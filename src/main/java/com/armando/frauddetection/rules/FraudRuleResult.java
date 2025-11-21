package com.armando.frauddetection.rules;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FraudRuleResult {

    private boolean triggered;
    private String reason;
}
