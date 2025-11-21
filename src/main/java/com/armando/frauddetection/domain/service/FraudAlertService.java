package com.armando.frauddetection.domain.service;

import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.FraudAlertRepository;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import com.armando.frauddetection.rules.FraudRulesEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionEventRepository transactionEventRepository;
    private final FraudRulesEngine fraudRulesEngine;

    /**
     * Evalúa reglas de fraude para una transacción existente (por transactionId),
     * persiste las alertas y actualiza el estado de la transacción.
     */
    public List<FraudAlert> getAlertsByTransactionId(String transactionId) {

        TransactionEvent event = transactionEventRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found: " + transactionId));

        return evaluateAndPersist(event);
    }

    /**
     * Evalúa las reglas para una transacción dada, actualiza la transacción
     * (flagged / flagReason) y persiste las alertas generadas.
     */
    public List<FraudAlert> evaluateAndPersist(TransactionEvent event) {

        // 1. Ejecutar motor de reglas
        List<FraudAlert> alerts = fraudRulesEngine.evaluate(event);

        // 2. Actualizar transacción según resultado
        if (!alerts.isEmpty()) {
            event.setFlagged(Boolean.TRUE);

            String reasons = alerts.stream()
                    .map(FraudAlert::getDescription)
                    .collect(Collectors.joining(" | "));

            event.setFlagReason(reasons);
        } else {
            event.setFlagged(Boolean.FALSE);
            event.setFlagReason(null);
        }

        // 3. Persistir cambios de la transacción
        transactionEventRepository.save(event);

        // 4. Persistir alertas si existen
        if (!alerts.isEmpty()) {
            fraudAlertRepository.saveAll(alerts);
        }

        return alerts;
    }
}
