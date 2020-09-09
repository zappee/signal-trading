package com.remal.signaltrading.tradehistory.service.coinbase;

import com.remal.signaltrading.exchange.coinbase.api.exchange.CoinbaseExchange;
import com.remal.signaltrading.tradehistory.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Coinbase products REST client.
 *
 * @author arnold.somogyi@gmail.com
 */
@Slf4j
public class ProductsService {

    /**
     * Coinbase REST API endpoint.
     */
    public static final String ENDPOINT = "/products";

    private CoinbaseExchange exchange;

    /**
     * Constructor.
     *
     * @param exchange coinbase REST API caller
     */
    public ProductsService(final CoinbaseExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Coinbase REST endpoint caller.
     *
     * @return product list
     */
    public List<Product> getProducts() {
        log.trace("Getting products...");
        List<Product> products = exchange.getAsList(ENDPOINT, new ParameterizedTypeReference<Product[]>() { });
        log.trace("Number of products: " + products.size());
        if (!products.isEmpty()) {
            log.debug(products.stream().map(Product::toString).collect(Collectors.joining()));
        }

        return products;
    }
}
