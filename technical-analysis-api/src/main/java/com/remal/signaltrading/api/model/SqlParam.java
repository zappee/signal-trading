package com.remal.signaltrading.api.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * While collecting data for the charts many SQLs are executed parallelly in the background.
 * This POJO holds information about the SQL queries that will be executed.
 *
 * @author arnold.somogyi@gmail.com
 */
@Getter
@Builder
@ToString
public class SqlParam {
    private Instant startOfPeriod;
    private Instant endOfPeriod;
}
