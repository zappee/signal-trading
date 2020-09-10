package com.remal.signaltrading.tradehistory;

import com.remal.signaltrading.tradehistory.task.CoinbaseAccountsTask;
import com.remal.signaltrading.tradehistory.task.CoinbaseCandlesTask;
import com.remal.signaltrading.tradehistory.task.CoinbaseProductsTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner configuration, executes tasks.
 *
 * @author arnold.somogyi@gmail.com
 */
@Component
@Slf4j
public class CommandLineArgParser implements CommandLineRunner {

    private ThreadPoolTaskScheduler taskScheduler;
    private ObjectProvider<CoinbaseCandlesTask> coinbaseCandlesPrototype;
    private CoinbaseAccountsTask coinbaseAccountsTask;
    private CoinbaseProductsTask coinbaseProductsTask;

    @Value("${exchange.coinbase.scheduler-delay}")
    private long delay;

    /**
     * Constructor.
     *
     * @param taskScheduler spring task scheduler
     * @param coinbaseCandlesPrototype candle API caller
     * @param coinbaseAccountsTask account API caller
     * @param coinbaseProductsTask product API caller
     */
    public CommandLineArgParser(ThreadPoolTaskScheduler taskScheduler,
                                ObjectProvider<CoinbaseCandlesTask> coinbaseCandlesPrototype,
                                CoinbaseAccountsTask coinbaseAccountsTask,
                                CoinbaseProductsTask coinbaseProductsTask) {

        this.taskScheduler = taskScheduler;
        this.coinbaseCandlesPrototype = coinbaseCandlesPrototype;
        this.coinbaseAccountsTask = coinbaseAccountsTask;
        this.coinbaseProductsTask = coinbaseProductsTask;
    }

    @Override
    public void run(String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Illegal arguments given.");
        }

        switch (args[0]) {
            case "candles":
                taskScheduler.scheduleWithFixedDelay(coinbaseCandlesPrototype.getObject("ETH-EUR"), delay);
                taskScheduler.scheduleWithFixedDelay(coinbaseCandlesPrototype.getObject("BTC-EUR"), delay);
                taskScheduler.scheduleWithFixedDelay(coinbaseCandlesPrototype.getObject("XRP-EUR"), delay);
                taskScheduler.scheduleWithFixedDelay(coinbaseCandlesPrototype.getObject("ETH-USD"), delay);
                break;

            case "accounts":
                coinbaseAccountsTask.run();
                System.exit(0);
                break;

            case "products":
                coinbaseProductsTask.run();
                System.exit(0);
                break;

            default:
                log.error("Invalid parameter was given.");
        }
    }
}
