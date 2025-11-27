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
        // given
        TransactionEvent event = TransactionEvent.builder()
                .accountId("ACC-123")
                .timestamp(OffsetDateTime.now())
                .build();

        // simulamos que en la ventana de tiempo hay 3 transacciones o más
        when(transactionEventRepository
                .countByAccountIdAndTimestampAfter(eq("ACC-123"), any(OffsetDateTime.class)))
                .thenReturn(3L);

        // when
        FraudRuleResult result = velocityRule.apply(event);

        // then
        assertTrue(result.isTriggered());
        assertNotNull(result.getReason());
        assertTrue(result.getReason().contains("ACC-123"));
        assertTrue(result.getReason().contains("High transaction velocity"));
    }

    @Test
    void apply_shouldNotTriggerWhenRecentCountIsBelowThreshold() {
        // given
        TransactionEvent event = TransactionEvent.builder()
                .accountId("ACC-456")
                .timestamp(OffsetDateTime.now())
                .build();

        when(transactionEventRepository
                .countByAccountIdAndTimestampAfter(eq("ACC-456"), any(OffsetDateTime.class)))
                .thenReturn(1L);

        // when
        FraudRuleResult result = velocityRule.apply(event);

        // then
        assertFalse(result.isTriggered());
        assertNull(result.getReason());
    }

    @Test
    void apply_shouldNotTriggerWhenAccountIdOrTimestampAreMissing() {
        // given
        TransactionEvent noAccount = TransactionEvent.builder()
                .accountId(null)
                .timestamp(OffsetDateTime.now())
                .build();

        TransactionEvent noTimestamp = TransactionEvent.builder()
                .accountId("ACC-789")
                .timestamp(null)
                .build();

        // when
        FraudRuleResult resultNoAccount = velocityRule.apply(noAccount);
        FraudRuleResult resultNoTimestamp = velocityRule.apply(noTimestamp);

        // then
        assertFalse(resultNoAccount.isTriggered());
        assertNull(resultNoAccount.getReason());

        assertFalse(resultNoTimestamp.isTriggered());
        assertNull(resultNoTimestamp.getReason());

        // y en estos casos no debería llamar al repo
        verify(transactionEventRepository, never())
                .countByAccountIdAndTimestampAfter(anyString(), any(OffsetDateTime.class));
    }
}
