package com.remal.signaltrading.tradehistory.config;

import com.remal.signaltrading.exchange.coinbase.api.exchange.CoinbaseExchange;
import com.remal.signaltrading.tradehistory.service.coinbase.AccountsService;
import com.remal.signaltrading.tradehistory.service.coinbase.CandlesService;
import com.remal.signaltrading.tradehistory.service.coinbase.ProductsService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * REST client configuration.
 *
 * @author arnold.somogyi@gmail.com
 */
@SpringBootConfiguration
public class RestServiceConfiguration {

    @Bean
    public AccountsService accountsService(CoinbaseExchange exchange) {
        return new AccountsService(exchange);
    }

    @Bean
    public CandlesService candlesService(CoinbaseExchange exchange) {
        return new CandlesService(exchange);
    }

    @Bean
    public ProductsService productsService(CoinbaseExchange exchange) {
        return new ProductsService(exchange);
    }
}
