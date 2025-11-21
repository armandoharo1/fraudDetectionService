package com.armando.frauddetection.domain.repository;

import com.armando.frauddetection.domain.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

    List<FraudAlert> findByTransactionId(String transactionId);
}
