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

    // ID de la transacción que viene del core/banco
    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    private String channel;        // WEB, MOBILE, ATM, POS, etc.
    private String ipAddress;
    private String country;
    private String merchantId;

    // Estos campos los iremos usando con las reglas
    private BigDecimal riskScore;
    private Boolean flagged;       // ¿marcada como sospechosa?
    private String flagReason;     // regla que disparó la alerta
}
