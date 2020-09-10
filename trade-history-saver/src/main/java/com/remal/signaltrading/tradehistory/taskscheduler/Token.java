package com.remal.signaltrading.tradehistory.taskscheduler;

import java.time.Instant;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Token holds information about the paralell execution of REST API calls.
 *
 * @author arnold.somogyi@gmail.com
 */
@Builder
@Getter
@Setter
@ToString
public class Token {
    private String id;
    private String taskId;
    private Long startAt;
    private Long finishedAt;
}
