package com.remal.signaltrading.tradehistory.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Account data structure.
 *
 * @author arnold.somogyi@gmail.com
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Account {
    private String id;
    private String currency;
    private BigDecimal balance;
    private BigDecimal available;
    private BigDecimal hold;
    private String profileId;
}
