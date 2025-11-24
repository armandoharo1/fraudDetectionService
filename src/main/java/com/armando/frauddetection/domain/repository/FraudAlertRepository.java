package com.armando.frauddetection.domain.repository;

import com.armando.frauddetection.domain.model.FraudAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

    Page<FraudAlert> findAllByTransactionId(String transactionId, Pageable pageable);

    Page<FraudAlert> findAllBySeverity(String severity, Pageable pageable);
}
