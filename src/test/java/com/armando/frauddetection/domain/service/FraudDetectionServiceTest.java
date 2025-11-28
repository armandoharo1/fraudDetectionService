package com.armando.frauddetection.domain.service;

import com.armando.frauddetection.api.controller.dto.DetectFraudRequest;
import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.FraudAlertRepository;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import com.armando.frauddetection.rules.FraudRulesEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private TransactionEventRepository transactionEventRepository;

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private FraudRulesEngine fraudRulesEngine;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @Test
    void buildEvent_shouldMapFieldsAndSetDefaults() {
        // given
        DetectFraudRequest request = new DetectFraudRequest(
                "TX-100",
                "ACC-200",
                BigDecimal.valueOf(1500.50),
                "USD",
                "WEB",
                "10.0.0.1",
                "US",
                "M-999"
        );


        TransactionEvent event = fraudDetectionService.buildEvent(request);


        assertEquals("TX-100", event.getTransactionId());
        assertEquals("ACC-200", event.getAccountId());
        assertEquals(BigDecimal.valueOf(1500.50), event.getAmount());
        assertEquals("USD", event.getCurrency());
        assertEquals("WEB", event.getChannel());
        assertEquals("10.0.0.1", event.getIpAddress());
        assertEquals("US", event.getCountry());
        assertEquals("M-999", event.getMerchantId());

        assertNotNull(event.getTimestamp(), "timestamp should be set");
        assertEquals(Boolean.FALSE, event.getFlagged(), "flagged should be false by default");
        assertNull(event.getRiskScore(), "riskScore should start null before rules");
        assertNull(event.getFlagReason(), "flagReason should start null");
    }

    @Test
    void saveEvent_shouldDelegateToRepository() {
        TransactionEvent event = TransactionEvent.builder()
                .transactionId("TX-200")
                .build();

        when(transactionEventRepository.save(event)).thenReturn(event);

        TransactionEvent result = fraudDetectionService.saveEvent(event);

        // then
        assertSame(event, result);
        verify(transactionEventRepository, times(1)).save(event);
    }

    @Test
    void evaluateRules_shouldPersistAlertsWhenNotEmpty() {
        // given
        TransactionEvent event = TransactionEvent.builder()
                .transactionId("TX-300")
                .build();

        FraudAlert alert1 = FraudAlert.builder()
                .transactionId("TX-300")
                .ruleCode("HighAmountRule")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .transactionId("TX-300")
                .ruleCode("RiskyCountryRule")
                .build();

        List<FraudAlert> alerts = List.of(alert1, alert2);

        when(fraudRulesEngine.evaluate(event)).thenReturn(alerts);

        List<FraudAlert> result = fraudDetectionService.evaluateRules(event);

        assertEquals(2, result.size());
        assertTrue(result.contains(alert1));
        assertTrue(result.contains(alert2));

        verify(fraudAlertRepository, times(1)).saveAll(alerts);
    }

    @Test
    void evaluateRules_shouldNotPersistWhenEmpty() {
        TransactionEvent event = TransactionEvent.builder()
                .transactionId("TX-400")
                .build();

        when(fraudRulesEngine.evaluate(event)).thenReturn(List.of());

        List<FraudAlert> result = fraudDetectionService.evaluateRules(event);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(fraudAlertRepository, never()).saveAll(anyList());
    }

    @Test
    void calculateRiskScore_shouldReturnZeroWhenNoAlerts() {
        BigDecimal result1 = fraudDetectionService.calculateRiskScore(null);
        BigDecimal result2 = fraudDetectionService.calculateRiskScore(List.of());

        assertEquals(BigDecimal.ZERO, result1);
        assertEquals(BigDecimal.ZERO, result2);
    }

    @Test
    void calculateRiskScore_shouldUseWeightsPerRule() {
        // given
        FraudAlert highAmount = FraudAlert.builder()
                .ruleCode("HighAmountRule")
                .build(); // weight 50

        FraudAlert riskyCountry = FraudAlert.builder()
                .ruleCode("RiskyCountryRule")
                .build(); // weight 60

        FraudAlert velocity = FraudAlert.builder()
                .ruleCode("VelocityRule")
                .build(); // weight 40

        FraudAlert unknown = FraudAlert.builder()
                .ruleCode("SomeFutureRule")
                .build(); //  default weight 10

        List<FraudAlert> alerts = List.of(highAmount, riskyCountry, velocity, unknown);


        BigDecimal score = fraudDetectionService.calculateRiskScore(alerts);

        // 50 + 60 + 40 + 10 = 160
        assertEquals(BigDecimal.valueOf(160), score);
    }
}
