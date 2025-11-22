package com.armando.frauddetection.domain.service;

import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import com.armando.frauddetection.domain.repository.FraudAlertRepository;
import com.armando.frauddetection.domain.repository.TransactionEventRepository;
import com.armando.frauddetection.rules.FraudRulesEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionEventRepository transactionEventRepository;
    private final FraudRulesEngine fraudRulesEngine;

    /**
     * Peso (puntaje) que aporta cada regla cuando se dispara.
     */
    private static final Map<String, Integer> RULE_WEIGHTS = Map.of(
            "HighAmountRule", 60,
            "RiskyCountryRule", 40
            // Aquí luego puedes añadir más reglas: "VelocityRule", etc.
    );

    /**
     * Método usado por TransactionController:
     * recibe el TransactionEvent ya persistido, evalúa reglas,
     * actualiza riskScore / flagged / flagReason y guarda las alertas.
     */
    public List<FraudAlert> evaluateAndPersist(TransactionEvent event) {

        // 1. Ejecutar motor de reglas → genera las alertas
        List<FraudAlert> alerts = fraudRulesEngine.evaluate(event);

        // 2. Calcular score total y severidades
        int totalScore = 0;
        StringBuilder combinedReason = new StringBuilder();

        for (FraudAlert alert : alerts) {

            String ruleCode = alert.getRuleCode();
            int ruleScore = RULE_WEIGHTS.getOrDefault(ruleCode, 0);
            totalScore += ruleScore;

            // severidad por regla
            alert.setSeverity(mapSeverity(ruleScore));

            // concatenar descripción en flagReason
            if (combinedReason.length() > 0) {
                combinedReason.append(" | ");
            }
            combinedReason.append(alert.getDescription());
        }

        int normalizedScore = Math.min(totalScore, 100);

        // 3. Actualizar la transacción con info de riesgo
        if (!alerts.isEmpty()) {
            event.setRiskScore(BigDecimal.valueOf(normalizedScore));
            event.setFlagged(true);
            event.setFlagReason(combinedReason.toString());
        } else {
            event.setRiskScore(BigDecimal.ZERO);
            event.setFlagged(false);
            event.setFlagReason(null);
        }

        // 4. Persistir cambios en BD
        transactionEventRepository.save(event);      // riskScore / flagged / flagReason
        if (!alerts.isEmpty()) {
            fraudAlertRepository.saveAll(alerts);    // alertas
        }

        return alerts;
    }

    /**
     * Método extra por si quieres evaluar a partir de un transactionId.
     * Internamente solo busca el evento y reutiliza evaluateAndPersist.
     */
    public List<FraudAlert> getAlertsByTransactionId(String transactionId) {

        TransactionEvent event = transactionEventRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        return evaluateAndPersist(event);
    }

    private String mapSeverity(int score) {
        if (score >= 70) {
            return "HIGH";
        } else if (score >= 40) {
            return "MEDIUM";
        }
        return "LOW";
    }
}
