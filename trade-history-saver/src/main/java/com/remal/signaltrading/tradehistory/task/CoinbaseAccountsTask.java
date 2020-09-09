package com.remal.signaltrading.tradehistory.task;

import com.remal.signaltrading.tradehistory.service.coinbase.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This is a task, executed by CommandLineRunner.
 *
 * @author arnold.somogyi@gmail.com
 */
@Component
@Slf4j
public class CoinbaseAccountsTask {

    private final AccountsService service;

    /**
     * Spring injects dependencies in constructor without @Autowired annotation.
     *
     * @param service service to inject
     */
    public CoinbaseAccountsTask(AccountsService service) {
        this.service = service;
    }

    /**
     * Business logic executor.
     */
    public void run() {
        service.getAccounts();
    }
}
