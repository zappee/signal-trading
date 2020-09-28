package com.remal.signaltrading.tradehistory.service.coinbase;

import java.util.List;
import java.util.stream.Collectors;

import com.remal.signaltrading.exchange.coinbase.api.exchange.CoinbaseExchange;
import com.remal.signaltrading.tradehistory.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

/**
 * Coinbase accounts REST client.
 *
 * @author arnold.somogyi@gmail.com
 */
@Slf4j
public class AccountsService {

    /**
     * Coinbase REST API endpoint.
     */
    public static final String ENDPOINT = "/accounts";

    private final CoinbaseExchange exchange;

    /**
     * Constructor.
     *
     * @param exchange coinbase REST API caller
     */
    public AccountsService(final CoinbaseExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Coinbase REST endpoint caller.
     *
     * @return account list
     */
    public List<Account> getAccounts() {
        log.trace("Getting accounts...");
        List<Account> accounts = exchange.getAsList(ENDPOINT, new ParameterizedTypeReference<>() { });
        log.trace("You have " + accounts.size() + " accounts");
        if (!accounts.isEmpty()) {
            log.debug(accounts.stream().map(Account::toString).collect(Collectors.joining()));
        }

        return accounts;
    }
}
