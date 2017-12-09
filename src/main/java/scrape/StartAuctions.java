package scrape;

import commons.ScrapeCommons;
import database.DatabaseQueries;
import database.DatabaseRecord;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by bartek on 2017-07-13.
 */
public class StartAuctions {

    private DatabaseQueries databaseQueries;
    private String url;
    private List<Timer> timerList;

    public StartAuctions(int limit) {

        timerList = new ArrayList<>();
        databaseQueries = new DatabaseQueries();
        // Do something about limit if action time/date has passed
        List<DatabaseRecord> urlList = databaseQueries.getAuctionsFromWatchList(limit);

        if(urlList.size() > 0) {
            for (int i = 0; i < urlList.size(); i++) {
                DatabaseRecord databaseRecord = urlList.get(i);
                //this.url = databaseRecord.getUrl();
                setTimer(databaseRecord.getUrl(), databaseRecord.getauctionStartInSeconds());
                System.out.println("Auction url: " + databaseRecord.getUrl());
                System.out.println("Auction starting in: " + databaseRecord.getauctionStartInSeconds() / 60 + " minutes");

                System.out.println("***");
            }
        } else {
            System.out.println("No auctions to start!");
        }

    }

    public void setTimer(String url, long seconds) {

        Timer timer = new Timer();
        timerList.add(timer);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    scrapeAuction(url);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (HttpException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, seconds * 1000);
    }

    public void scrapeAuction(String url) throws InterruptedException, ExecutionException, HttpException, IOException {
        System.out.println("Starting auction .. " + url);
        ScrapeAuction scrapeAuction = new ScrapeAuction(url, 0);
    }

    public static void main(String[] args) {
        int limit = Integer.parseInt(args[0]);
        StartAuctions startAuctions = new StartAuctions(limit);
    }
}
