package com.portfolio.stockanalyzer.service;

import com.portfolio.stockanalyzer.dto.request.CreateAlertRequest;
import com.portfolio.stockanalyzer.dto.response.AlertResponse;
import com.portfolio.stockanalyzer.entity.Alert;
import com.portfolio.stockanalyzer.entity.AlertType;
import com.portfolio.stockanalyzer.entity.Stock;
import com.portfolio.stockanalyzer.entity.User;
import com.portfolio.stockanalyzer.exception.ResourceNotFoundException;
import com.portfolio.stockanalyzer.repository.AlertRepository;
import com.portfolio.stockanalyzer.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public List<AlertResponse> getUserAlerts(Long userId) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(AlertResponse::from)
                .toList();
    }

    @Transactional
    public AlertResponse createAlert(User user, CreateAlertRequest request) {
        Stock stock = stockRepository.findBySymbol(request.symbol().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found: " + request.symbol()));

        Alert alert = Alert.builder()
                .user(user)
                .stock(stock)
                .alertType(request.alertType())
                .targetValue(request.targetValue())
                .triggered(false)
                .build();
        alertRepository.save(alert);
        log.info("Alert created: {} {} {} for user {}",
                request.alertType(), stock.getSymbol(), request.targetValue(), user.getEmail());
        return AlertResponse.from(alert);
    }

    @Transactional
    public void deleteAlert(Long userId, Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));
        alertRepository.delete(alert);
    }

    @Transactional
    public List<Alert> checkAndTriggerAlerts() {
        List<Alert> activeAlerts = alertRepository.findActiveAlerts();
        List<Alert> triggered = new java.util.ArrayList<>();

        for (Alert alert : activeAlerts) {
            BigDecimal currentPrice = alert.getStock().getCurrentPrice();
            BigDecimal previousClose = alert.getStock().getPreviousClose();
            boolean shouldTrigger = switch (alert.getAlertType()) {
                case PRICE_ABOVE -> currentPrice.compareTo(alert.getTargetValue()) >= 0;
                case PRICE_BELOW -> currentPrice.compareTo(alert.getTargetValue()) <= 0;
                case PERCENT_CHANGE_UP -> {
                    if (previousClose == null || previousClose.compareTo(BigDecimal.ZERO) == 0) yield false;
                    BigDecimal change = currentPrice.subtract(previousClose)
                            .divide(previousClose, 4, java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    yield change.compareTo(alert.getTargetValue()) >= 0;
                }
                case PERCENT_CHANGE_DOWN -> {
                    if (previousClose == null || previousClose.compareTo(BigDecimal.ZERO) == 0) yield false;
                    BigDecimal change = previousClose.subtract(currentPrice)
                            .divide(previousClose, 4, java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    yield change.compareTo(alert.getTargetValue()) >= 0;
                }
            };

            if (shouldTrigger) {
                alert.setTriggered(true);
                alert.setTriggeredAt(LocalDateTime.now());
                alertRepository.save(alert);
                triggered.add(alert);
                log.info("Alert triggered: {} {} for {}", alert.getAlertType(),
                        alert.getStock().getSymbol(), alert.getUser().getEmail());
            }
        }
        return triggered;
    }
}
