package com.armando.frauddetection.api.controller.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TransactionEventRequest(

        @NotBlank(message = "transactionId is required")
        String transactionId,

        @NotBlank(message = "accountId is required")
        String accountId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message = "currency is required")
        @Size(max = 10, message = "currency must have at most 10 characters")
        String currency,

        @NotBlank(message = "channel is required")
        String channel,

        @NotBlank(message = "ipAddress is required")
        String ipAddress,

        @NotBlank(message = "country is required")
        @Size(min = 2, max = 5, message = "country must have between 2 and 5 characters")
        String country,

        @NotBlank(message = "merchantId is required")
        String merchantId
) {}
