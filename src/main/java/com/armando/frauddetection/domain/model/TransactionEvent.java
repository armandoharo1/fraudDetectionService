package com.armando.frauddetection.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transaction_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    private String channel;        // WEB, MOBILE, ATM, POS, etc.
    private String ipAddress;
    private String country;
    private String merchantId;

    // Campos relacionados al riesgo
    private BigDecimal riskScore;
    private Boolean flagged;       // ¿marcada como sospechosa?
    private String flagReason;     // regla(s) que dispararon la alerta
}
