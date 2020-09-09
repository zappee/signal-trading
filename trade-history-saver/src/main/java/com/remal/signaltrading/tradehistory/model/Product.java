package com.remal.signaltrading.tradehistory.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Product data structure.
 *
 * @author arnold.somogyi@gmail.com
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Product {
    private String id;
    private String baseCurrency;
    private String quoteCurrency;
    private Double baseMinSize;
    private Double baseMaxSize;
    private Double quoteIncrement;
    private double baseIncrement;
    private String displayName;
    private String status;
    private Boolean marginEnabled;
    private String statusMessage;
    private BigDecimal minMarketFunds;
    private Integer maxMarketFunds;
    private Boolean postOnly;
    private Boolean limitOnly;
    private Boolean cancelOnly;
    private String type;
}
