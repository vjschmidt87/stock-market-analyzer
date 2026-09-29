package com.portfolio.stockanalyzer.dto.request;

import com.portfolio.stockanalyzer.entity.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAlertRequest(
        @NotBlank String symbol,
        @NotNull AlertType alertType,
        @NotNull @Positive BigDecimal targetValue
) {}
