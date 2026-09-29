package com.portfolio.stockanalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_snapshots", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"stock_id", "date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal lowPrice;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal closePrice;

    @Column(nullable = false)
    private Long volume;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockSnapshot that = (StockSnapshot) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
