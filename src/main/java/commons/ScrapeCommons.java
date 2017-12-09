package commons;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by bartek on 2017-07-11.
 */
public class ScrapeCommons {

    public String getRandomUserAgent() {
        List<String> userAgents = new ArrayList<>();
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
        userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0");
        userAgents.add("Mozilla/5.0 (X11; OpenBSD amd64; rv:28.0) Gecko/20100101 Firefox/28.0");

        Random ran = new Random();
        int x = ran.nextInt(userAgents.size()) + 0;

        return userAgents.get(x);

    }

    public static String getGeckoDriver() {
        OSValidator osValidator = new OSValidator();

        String geckoDriver = "";

        if(osValidator.isMac()) {
            geckoDriver =  "geckodriver-mac";
        }
        else if (osValidator.isUnix()) {
            geckoDriver = "geckodriver-unix";
        }

        return geckoDriver;
    }

    public static String currentTime() {

        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = zonedDateTime.format(dateTimeFormatter);

        return currentTime;
    }

    public ZonedDateTime convertTimeStringToDate(String timeString) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime = null;
        try {
            Date date = simpleDateFormat.parse(timeString);
            zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

            //System.out.println(zonedDateTime.format(DateTimeFormatter.ofPattern(pattern)));
            //System.out.println("date : " + simpleDateFormat.format(date));
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }

        return zonedDateTime;
    }

    public List<Integer> convertTimeStringToList(String timeString) {
        String[] timeStringSplit = timeString.split(":");
        int hours = Integer.parseInt(timeStringSplit[0]);
        int minutes = Integer.parseInt(timeStringSplit[1]);
        int seconds = Integer.parseInt(timeStringSplit[2]);
        //System.out.println("Hours: " + hours + ", minutes: " + minutes + ", seconds: " + seconds);

        List<Integer> timeList = new ArrayList<>();
        timeList.add(hours);
        timeList.add(minutes);
        timeList.add(seconds);

        return timeList;
    }

    public int convertTimeStringToSeconds(String timeString) {
        String[] timeStringSplit = timeString.split(":");
        int hours = Integer.parseInt(timeStringSplit[0]);
        int minutes = Integer.parseInt(timeStringSplit[1]);
        int seconds = Integer.parseInt(timeStringSplit[2]);
        //System.out.println("Hours: " + hours + ", minutes: " + minutes + ", seconds: " + seconds);

        int timeInSeconds = hours * 3600 + minutes * 60 + seconds;

        return timeInSeconds;
    }

    // Convert time-string to Date (only time)

    public String addTimeToCurrentDate(List<Integer> timeList) {

        int hours = timeList.get(0);
        int minutes = timeList.get(1);
        int seconds = timeList.get(2);
        System.out.println("Hours: " + hours + ", minutes: " + minutes + ", seconds: " + seconds);

        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println("Current date: " + zonedDateTime);

        if(hours > 0) {
            zonedDateTime = zonedDateTime.plusHours((long)hours);
        }
        if(minutes > 0) {
            zonedDateTime = zonedDateTime.plusMinutes((long)minutes);
        }
        if(seconds > 0) {
            zonedDateTime = zonedDateTime.plusSeconds((long)seconds);
        }

        System.out.println("Start date: " + zonedDateTime);

        String formattedDate = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("Formatted start date: " + formattedDate);

        return formattedDate;
    }

}
