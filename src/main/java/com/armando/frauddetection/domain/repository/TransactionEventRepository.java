package com.armando.frauddetection.domain.repository;

import com.armando.frauddetection.domain.model.TransactionEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, Long> {

    Optional<TransactionEvent> findByTransactionId(String transactionId);

    long countByAccountIdAndTimestampAfter(String accountId, OffsetDateTime timestamp);

    // Nuevos métodos para las consultas paginadas
    Page<TransactionEvent> findAllByFlagged(Boolean flagged, Pageable pageable);

    Page<TransactionEvent> findAllByRiskScoreGreaterThanEqual(BigDecimal riskScore, Pageable pageable);

    Page<TransactionEvent> findAllByAccountId(String accountId, Pageable pageable);
}
