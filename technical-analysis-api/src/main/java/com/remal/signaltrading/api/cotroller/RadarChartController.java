package com.remal.signaltrading.api.cotroller;

import java.time.Instant;
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
     * <p>Example GET REST call: http://localhost:8081/api/radar?ticker=ee&interval=300&scale=60</p>
     *
     * @param ticker product identifier number
     * @param interval interval in seconds
     * @param scale interval in seconds
     * @return data what can be used to generate chart
     */
    @RequestMapping("/radar")
    public ResponseEntity<String> radarChart(@RequestParam String ticker,
                                             @RequestParam long interval,
                                             @RequestParam long scale) {

        if (validateRequest(interval, scale)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Greetings from Spring Boot!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getHelpMessage());
        }
    }

    private String getHelpMessage() {
        String liBegin = "<li>";
        String liEnd = "</il>";

        StringBuilder sb = new StringBuilder()
                .append(Instant.now().toString()).append(": Invalid interval or scale.")
                .append("<p>Possible values: </p>")
                .append("<ul>")

                .append(liBegin)
                .append(Interval.FIVE_MINUTES).append(": ")
                .append(Interval.getPossibleScales(Interval.FIVE_MINUTES))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.FIFTEEN_MINUTES).append(": ")
                .append(Interval.getPossibleScales(Interval.FIFTEEN_MINUTES))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.THIRTY_MINUTES).append(": ")
                .append(Interval.getPossibleScales(Interval.THIRTY_MINUTES))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.ONE_HOUR).append(": ")
                .append(Interval.getPossibleScales(Interval.ONE_HOUR))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.TWO_HOURS).append(": ")
                .append(Interval.getPossibleScales(Interval.TWO_HOURS))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.FOUR_HOURS).append(": ")
                .append(Interval.getPossibleScales(Interval.FOUR_HOURS))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.EIGHT_HOURS).append(": ")
                .append(Interval.getPossibleScales(Interval.EIGHT_HOURS))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.ONE_DAY).append(": ")
                .append(Interval.getPossibleScales(Interval.ONE_DAY))
                .append(liEnd)

                .append(liBegin)
                .append(Interval.ONE_WEEK).append(": ")
                .append(Interval.getPossibleScales(Interval.ONE_WEEK))
                .append(liEnd)

                .append("</ul>");
        return sb.toString();
    }

    private boolean validateRequest(long interval, long scale) {
        Interval intervalEnum = Interval.valueOf(interval);
        Interval scaleEnum = Interval.valueOf(scale);
        EnumSet<Interval> possibleScales = Interval.getPossibleScales(intervalEnum);
        boolean validScale = possibleScales.contains(scaleEnum);
        return validScale && Interval.UNDEFINED != scaleEnum;
    }
}
