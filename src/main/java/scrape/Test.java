package scrape;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Created by bartek on 2017-07-19.
 */
public class Test {

    public static void main(String[] args) {
        Clock clock = Clock.system(ZoneId.systemDefault());
        System.out.println(clock);
        System.out.println(clock.instant());
        System.out.println(ZonedDateTime.now(clock));
        System.out.println(ZonedDateTime.now());


        // Convert zone date to time

        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedZoneDate = zonedDateTime.format(dateTimeFormatter);
        System.out.println(formattedZoneDate);
    }
}
