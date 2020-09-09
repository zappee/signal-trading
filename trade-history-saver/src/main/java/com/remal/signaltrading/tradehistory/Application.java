package com.remal.signaltrading.tradehistory;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Spring Boot application entry point.
 *
 * @author arnold.somogyi@gmail.com
 */
@SpringBootApplication
public class Application {

    /**
     * Entry point of the application.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
    }
}
