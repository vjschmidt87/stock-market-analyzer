package com.portfolio.stockanalyzer.dto.response;

import com.portfolio.stockanalyzer.entity.StockSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SnapshotResponse(
        LocalDate date,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Long volume
) {
    public static SnapshotResponse from(StockSnapshot snapshot) {
        return new SnapshotResponse(
                snapshot.getDate(),
                snapshot.getOpenPrice(), snapshot.getHighPrice(),
                snapshot.getLowPrice(), snapshot.getClosePrice(),
                snapshot.getVolume()
        );
    }
}
