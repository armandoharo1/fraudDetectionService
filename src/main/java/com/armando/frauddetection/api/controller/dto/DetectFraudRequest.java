package com.armando.frauddetection.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Fraud detection request payload")
public record DetectFraudRequest(

        @NotBlank(message = "transactionId is required")
        String transactionId,

        @NotBlank(message = "accountId is required")
        String accountId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message = "currency is required")
        String currency,

        @NotBlank(message = "channel is required")
        String channel,

        @NotBlank(message = "ipAddress is required")
        String ipAddress,

        @NotBlank(message = "country is required")
        String country,

        @NotBlank(message = "merchantId is required")
        String merchantId
) {}
