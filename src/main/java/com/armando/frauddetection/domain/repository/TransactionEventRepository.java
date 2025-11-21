package com.armando.frauddetection.domain.repository;

import com.armando.frauddetection.domain.model.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, Long> {

    Optional<TransactionEvent> findByTransactionId(String transactionId);
}
