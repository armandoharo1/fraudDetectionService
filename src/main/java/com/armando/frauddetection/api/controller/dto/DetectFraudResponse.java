package com.armando.frauddetection.api.controller.dto;

import com.armando.frauddetection.domain.model.FraudAlert;
import com.armando.frauddetection.domain.model.TransactionEvent;
import java.util.List;

public record DetectFraudResponse(
        TransactionEvent transaction,
        List<FraudAlert> alerts
) { }
