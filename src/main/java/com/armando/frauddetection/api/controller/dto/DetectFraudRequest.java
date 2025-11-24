package com.armando.frauddetection.api.controller.dto;

import java.math.BigDecimal;

public record DetectFraudRequest(
        String transactionId,
        String accountId,
        BigDecimal amount,
        String currency,
        String channel,
        String ipAddress,
        String country,
        String merchantId
) {}
