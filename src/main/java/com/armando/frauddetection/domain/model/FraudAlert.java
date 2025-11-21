package com.armando.frauddetection.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fraud_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String ruleCode;   // ej: HIGH_AMOUNT, RISKY_COUNTRY

    @Column(nullable = false)
    private String severity;   // LOW, MEDIUM, HIGH

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
