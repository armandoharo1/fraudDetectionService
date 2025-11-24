package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class VelocityRule implements FraudRule {

    private final TransactionEventRepository transactionEventRepository;

    private static final int MAX_TX_LAST_5_MIN = 3; // umbral
    private static final int WINDOW_MINUTES = 5;

    @Override
    public FraudRuleResult apply(TransactionEvent event) {
        if (event.getAccountId() == null || event.getTimestamp() == null) {
            return new FraudRuleResult(false, null);
        }

        OffsetDateTime since = event.getTimestamp().minusMinutes(WINDOW_MINUTES);

        long recentCount = transactionEventRepository
                .countByAccountIdAndTimestampAfter(event.getAccountId(), since);

        if (recentCount >= MAX_TX_LAST_5_MIN) {
            String reason = "High transaction velocity: " + recentCount +
                    " transactions in last " + WINDOW_MINUTES + " minutes for account " +
                    event.getAccountId();
            return new FraudRuleResult(true, reason);
        }

        return new FraudRuleResult(false, null);
    }
}
