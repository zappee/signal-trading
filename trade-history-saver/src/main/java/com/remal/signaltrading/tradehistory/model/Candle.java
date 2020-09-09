package com.remal.signaltrading.tradehistory.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Candle data structure.
 *
 * @author arnold.somogyi@gmail.com
 */
@Builder
@Getter
@ToString
public class Candle {
    private String productId;
    private Instant time;
    private BigDecimal lowestPrice;
    private BigDecimal highestPrice;
    private BigDecimal openingPrice;
    private BigDecimal closingPrice;
    private BigDecimal volume;
}
