package com.remal.signaltrading.api.cotroller;

import java.util.EnumSet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller.
 *
 * @author arnold.somogyi@gmail.com
 */
@RestController
public class RadarChartController {

    /**
     * Redar chart data provider endpoint.
     *
     * <p>Possible values for interval and scale are the followings:
     *    - 1 min
     *    - 5 minutes
     *    - 15 minutes
     *    - 30 minutes
     *    - 1 hour
     *    - 2 hours
     *    - 4 hours
     *    - 8 hours
     *    - 1 day
     *    - 1 week
     *    - 1 month</p>
     *
     * <p>Example GET REST call: http://localhost:8081/api/radar?ticker=ee&interval=300000&scale=60000</p>
     *
     * @param ticker product identifier number
     * @param interval interval in milliseconds
     * @param scale interval in milliseconds
     * @return data what can be used to generate chart
     */
    @RequestMapping("/radar")
    public ResponseEntity<String> radarChart(@RequestParam String ticker,
                                             @RequestParam long interval,
                                             @RequestParam long scale) {

        Interval intervalEnum = Interval.valueOf(interval);
        Interval scaleEnum = Interval.valueOf(scale);
        boolean validRequest = validateRequest(intervalEnum, scaleEnum);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Greetings from Spring Boot! " + validRequest);
    }

    private boolean validateRequest(Interval intervalEnum, Interval scaleEnum) {
        EnumSet<Interval> possibleScales = Interval.getPossibleScales(intervalEnum);
        boolean validScale = possibleScales.contains(scaleEnum);
        return validScale && Interval.UNDEFINED != scaleEnum;
    }
}
