package com.remal.signaltrading.tradehistory.service.coinbase;

/**
 * <p>The granularity field in Coinbase must be one of the following values:
 * {60, 300, 900, 3600, 21600, 86400}.</p>
 *
 * <p>Otherwise, your request will be rejected. These values correspond to
 * timeslices representing one minute, five minutes, fifteen minutes, one hour,
 * six hours, and one day, respectively.</p>
 *
 * @author arnold.somogyi@gmail.com
 */
public enum Granularity {
    ONE_MINUTE(60),
    FIVE_MINUTES(300),
    FIFTEEN_MINUTES(900),
    ONE_HOUR(3600),
    SIX_HOURS(21600),
    ONE_DAY(86400);

    private long value;

    Granularity(long value) {
        this.value = value;
    }

    public long getSeconds() {
        return value;
    }

    public long getMilliseconds() {
        return value * 1000L;
    }
}
