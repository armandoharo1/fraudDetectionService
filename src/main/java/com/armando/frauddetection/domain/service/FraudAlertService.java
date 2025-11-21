package com.armando.frauddetection.domain.service;

import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.FraudAlertRepository;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import com.armando.frauddetection.rules.FraudRulesEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionEventRepository transactionEventRepository;
    private final FraudRulesEngine fraudRulesEngine;

    /**
     * Evalúa reglas de fraude para una transacción y retorna las alertas
     */
    public List<FraudAlert> getAlertsByTransactionId(String transactionId) {

        // 1. Obtener evento (o lanzar excepción si no existe)
        TransactionEvent event = transactionEventRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found: " + transactionId));

        // 2. Ejecutar motor de reglas
        List<FraudAlert> alerts = fraudRulesEngine.evaluate(event);

        // 3. Persistir alertas si existen
        if (!alerts.isEmpty()) {
            fraudAlertRepository.saveAll(alerts);
        }

        // 4. Devolver la lista final
        return alerts;
    }
}
