package com.remal.signaltrading.tradehistory.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.remal.signaltrading.exchange.coinbase.api.exchange.CoinbaseExchange;
import com.remal.signaltrading.exchange.coinbase.api.exchange.CoinbaseExchangeImpl;
import com.remal.signaltrading.exchange.coinbase.security.Signature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Coinbase REST client configuration.
 *
 * @author arnold.somogyi@gmail.com
 */
@SpringBootConfiguration
public class CoinbaseConfiguration {

    /**
     * Initialize JSON serializer.
     *
     * @return jackson object mapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * REST request signature generator.
     *
     * @param secret coinbase API secret
     * @return signature generator instance
     */
    @Bean
    public Signature signature(@Value("${exchange.coinbase.secret}") String secret) {
        return new Signature(secret);
    }

    /**
     * Coinbase REST API caller.
     *
     * @param publicKey your coinbase API public key
     * @param passphrase your coinbase API passphrase
     * @param baseUrl coinbase API base URL
     * @param signature request content signature generator
     * @param objectMapper JSON serializer
     * @return coinbase REST API caller
     */
    @Bean
    public CoinbaseExchange coinbasePro(@Value("${exchange.coinbase.key}") String publicKey,
                                        @Value("${exchange.coinbase.passphrase}") String passphrase,
                                        @Value("${exchange.coinbase.baseUrl}") String baseUrl,
                                        Signature signature,
                                        ObjectMapper objectMapper) {
        return new CoinbaseExchangeImpl(publicKey, passphrase, baseUrl, signature, objectMapper);
    }
}
