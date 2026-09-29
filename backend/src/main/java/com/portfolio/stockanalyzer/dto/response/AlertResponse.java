package com.portfolio.stockanalyzer.dto.response;

import com.portfolio.stockanalyzer.entity.Alert;
import com.portfolio.stockanalyzer.entity.AlertType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertResponse(
        Long id,
        String symbol,
        String companyName,
        AlertType alertType,
        BigDecimal targetValue,
        BigDecimal currentPrice,
        boolean triggered,
        LocalDateTime triggeredAt,
        LocalDateTime createdAt
) {
    public static AlertResponse from(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getStock().getSymbol(),
                alert.getStock().getCompanyName(),
                alert.getAlertType(),
                alert.getTargetValue(),
                alert.getStock().getCurrentPrice(),
                alert.isTriggered(),
                alert.getTriggeredAt(),
                alert.getCreatedAt()
        );
    }
}
