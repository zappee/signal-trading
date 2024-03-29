package com.remal.signaltrading.tradehistory.taskscheduler;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

import lombok.extern.slf4j.Slf4j;

/**
 * Token pattern implementation, manages parallel REST API calls.
 *
 * <p>Coinbase API is rate limited. When a rate limit is exceeded, a status of
 * 429 Too Many Requests will be returned. The coinbase public endpoints allows
 * 3 requests per second, up to 6 requests per second in bursts.</p>
 *
 * @author arnold.somogyi@gmail.com
 */
@Slf4j
public class TokenBucket {

    private static final Vector<Token> tokens = new Vector<>();

    /**
     * Coinbase public endpoints allows 3 requests per second
     */
    private final long lengthOfPeriod;

    /**
     * Coinbase public endpoints allows 3 requests per second
     */
    private final long maxRequestsWithinPeriod;

    /**
     * Constructor.
     *
     * @param lengthOfPeriod length of the period in milliseconds
     * @param maxRequestsWithinPeriod number of the maximum requests within the period
     */
    public TokenBucket(long lengthOfPeriod, long maxRequestsWithinPeriod) {
        this.lengthOfPeriod = lengthOfPeriod;
        this.maxRequestsWithinPeriod = maxRequestsWithinPeriod;
    }

    /**
     * Acquires a single execution permit from the given task.
     *
     * @param taskId identifier of the calling task
     * @return task ID belongs to the given task
     */
    public synchronized Optional<String> acquire(String taskId) {
        long now = System.currentTimeMillis();
        long runningTasks = tokens.stream().filter(token -> token.getFinishedAt() == null).count();
        long completedWithinPeriod = tokens.stream()
                .filter(token -> (token.getFinishedAt() != null) && (token.getFinishedAt() > now - lengthOfPeriod))
                .count();

        log.trace("running: {}, completed: {}, bucket: {}", runningTasks, completedWithinPeriod, tokens.toString());

        String tokenId = UUID.randomUUID().toString();
        if (runningTasks + completedWithinPeriod < maxRequestsWithinPeriod) {
            tokens.add(Token.builder().id(tokenId).taskId(taskId).startAt(now).build());
            return Optional.of(tokenId);
        }

        return Optional.empty();
    }

    /**
     * Freed the given execution permit.
     *
     * @param token execution permit token
     */
    public synchronized void release(String token) {
        long now = System.currentTimeMillis();
        tokens.stream()
                .filter(t -> t.getId().equals(token)).findFirst()
                .ifPresent(t -> t.setFinishedAt(now));
        maintaining();
    }

    private void maintaining() {
        long now = System.currentTimeMillis();
        tokens.removeIf(token -> (token.getFinishedAt() != null) && now - token.getFinishedAt() > lengthOfPeriod);
    }
}
