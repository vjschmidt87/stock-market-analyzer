package com.portfolio.stockanalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"snapshots", "analysisResults"})
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String symbol;

    @Column(nullable = false)
    private String companyName;

    private String sector;
    private String industry;
    private String exchange;
    private String description;
    private String logoUrl;

    private BigDecimal marketCap;
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
    private BigDecimal weekHigh52;
    private BigDecimal weekLow52;
    private Long volume;
    private Long avgVolume;
    private BigDecimal peRatio;
    private BigDecimal dividendYield;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<StockSnapshot> snapshots = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AnalysisResult> analysisResults = new ArrayList<>();

    private LocalDateTime lastUpdated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return id != null && id.equals(stock.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
