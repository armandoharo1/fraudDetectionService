package com.armando.frauddetection.rules;

import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VelocityRuleTest {

    @Mock
    private TransactionEventRepository transactionEventRepository;

    @InjectMocks
    private VelocityRule velocityRule;

    @Test
    void apply_shouldTriggerWhenRecentCountReachesThreshold() {
        TransactionEvent event = TransactionEvent.builder()
                .accountId("ACC-123")
                .timestamp(OffsetDateTime.now())
                .build();

        // This simulates that in the time window there are 3 or more transactions
        when(transactionEventRepository
                .countByAccountIdAndTimestampAfter(eq("ACC-123"), any(OffsetDateTime.class)))
                .thenReturn(3L);

        FraudRuleResult result = velocityRule.apply(event);

        assertTrue(result.isTriggered());
        assertNotNull(result.getReason());
        assertTrue(result.getReason().contains("ACC-123"));
        assertTrue(result.getReason().contains("High transaction velocity"));
    }

    @Test
    void apply_shouldNotTriggerWhenRecentCountIsBelowThreshold() {
        TransactionEvent event = TransactionEvent.builder()
                .accountId("ACC-456")
                .timestamp(OffsetDateTime.now())
                .build();

        when(transactionEventRepository
                .countByAccountIdAndTimestampAfter(eq("ACC-456"), any(OffsetDateTime.class)))
                .thenReturn(1L);

        FraudRuleResult result = velocityRule.apply(event);

        assertFalse(result.isTriggered());
        assertNull(result.getReason());
    }

    @Test
    void apply_shouldNotTriggerWhenAccountIdOrTimestampAreMissing() {
        TransactionEvent noAccount = TransactionEvent.builder()
                .accountId(null)
                .timestamp(OffsetDateTime.now())
                .build();

        TransactionEvent noTimestamp = TransactionEvent.builder()
                .accountId("ACC-789")
                .timestamp(null)
                .build();

        FraudRuleResult resultNoAccount = velocityRule.apply(noAccount);
        FraudRuleResult resultNoTimestamp = velocityRule.apply(noTimestamp);

        assertFalse(resultNoAccount.isTriggered());
        assertNull(resultNoAccount.getReason());

        assertFalse(resultNoTimestamp.isTriggered());
        assertNull(resultNoTimestamp.getReason());

        verify(transactionEventRepository, never())
                .countByAccountIdAndTimestampAfter(anyString(), any(OffsetDateTime.class));
    }
}
