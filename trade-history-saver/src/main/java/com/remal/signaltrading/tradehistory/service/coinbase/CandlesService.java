package com.remal.signaltrading.tradehistory.service.coinbase;

import com.remal.signaltrading.exchange.coinbase.api.exchange.CoinbaseExchange;
import com.remal.signaltrading.tradehistory.model.Candle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.core.ParameterizedTypeReference;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Coinbase candles REST client.
 *
 * @author arnold.somogyi@gmail.com
 */
@Slf4j
public class CandlesService {

    /**
     * Coinbase REST API endpoint.
     */
    private static final String ENDPOINT = "/products/%s/candles";

    private final CoinbaseExchange exchange;

    /**
     * Constructor.
     *
     * @param exchange coinbase REST API caller
     */
    public CandlesService(final CoinbaseExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Coinbase REST endpoint caller.
     *
     * @param productId coinbase product ID
     * @return candle list
     */
    public List<Candle> getCandles(String productId) {
        String endpoint = String.format(ENDPOINT, productId);
        String response = exchange.get(endpoint, new ParameterizedTypeReference<>() { });
        return parseResponse(productId, response);
    }

    /**
     * Coinbase REST endpoint caller.
     * https://docs.pro.coinbase.com/#get-historic-rates
     *
     * @param productId coinbase product ID
     * @param start start time
     * @param end end time
     * @param granularity desired timeslice in seconds
     * @return candle list
     */
    public List<Candle> getCandles(String productId, Instant start, Instant end, Granularity granularity) {
        String utcStart = start.toString();
        String utcEnd = end.toString();

        String endpoint = String.format(ENDPOINT, productId)
                + "?start=" + StringEscapeUtils.escapeHtml4(utcStart)
                + "&end=" + StringEscapeUtils.escapeHtml4(utcEnd)
                + "&granularity=" + granularity.getSeconds();

        log.debug("getting historical rates, endpoint: " + endpoint + "...");

        List<Candle> candles = new ArrayList<>();
        String response = exchange.get(endpoint, new ParameterizedTypeReference<>() { });
        if (Objects.nonNull(response)) {
            candles.addAll(parseResponse(productId, response));

            if (candles.isEmpty()) {
                log.debug("nothing to downloaded");
            } else {
                log.debug("{} candles have been downloaded: {}",
                        candles.size(),
                        candles.stream().map(Candle::toString).collect(Collectors.joining()));
            }
        }
        return candles;
    }

    private List<Candle> parseResponse(String productId, String response) {
        List<Candle> candles = new ArrayList<>();

        // remove the first and the last character
        response = response.replaceAll("^.|.$", "");

        // remove entry separators: "],[" -> "]["
        response = response.replaceAll("],\\[", "][");

        // split to candle groups: [...][...][...]
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(response);
        while (matcher.find()) {
            // group example: [1598120940,331.98,332.24,332.05,331.99,6.82750791]
            String group = matcher.group(1);
            String[] groupEntries = group.split(",");

            Candle candle = Candle
                    .builder()
                    .productId(productId)
                    .time(Instant.ofEpochMilli(Long.valueOf(groupEntries[0]) * 1000L)) // received date is in UNIX time
                    .lowestPrice(new BigDecimal(groupEntries[1]))
                    .highestPrice(new BigDecimal(groupEntries[2]))
                    .openingPrice(new BigDecimal(groupEntries[3]))
                    .closingPrice(new BigDecimal(groupEntries[4]))
                    .volume(new BigDecimal(groupEntries[5]))
                    .build();

            candles.add(candle);
        }
        return candles;
    }
}
