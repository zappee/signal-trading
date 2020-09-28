package com.remal.signaltrading.tradehistory.config;

import javax.sql.DataSource;

import com.remal.signaltrading.tradehistory.service.coinbase.CandlesService;
import com.remal.signaltrading.tradehistory.task.CoinbaseCandlesTask;
import com.remal.signaltrading.tradehistory.taskscheduler.TokenBucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration of the candles downloader task scheduler.
 *
 * @author arnold.somogyi@gmail.com
 */
@SpringBootConfiguration
public class TaskSchedulerConfiguration {

    private final TokenBucket tokenBucket;
    private final CandlesService candlesService;
    private final DataSource dataSource;

    /**
     * Initialize variables.
     *
     * @param candlesService candle RET API client
     * @param dataSource data source
     * @param allowedRequests allowed API requests within the period
     * @param periodLength length of the period
     */
    public TaskSchedulerConfiguration(CandlesService candlesService,
                                      DataSource dataSource,
                                      @Value("${exchange.coinbase.allowed-requests-within-period}") int allowedRequests,
                                      @Value("${exchange.coinbase.period-length}") int periodLength) {

        this.candlesService = candlesService;
        this.dataSource = dataSource;
        tokenBucket = new TokenBucket(periodLength, allowedRequests);
    }

    /**
     * Trade history downloader task producer.
     *
     * @param id product ticker
     * @return trade history downloader task
     */
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CoinbaseCandlesTask coinbaseCandlesTaskFactory(String id) {
        return new CoinbaseCandlesTask(id, tokenBucket, candlesService, dataSource);
    }

    /**
     * Initialization of Spring's TaskScheduler.
     *
     * @return the initialized scheduler
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        return threadPoolTaskScheduler;
    }
}
