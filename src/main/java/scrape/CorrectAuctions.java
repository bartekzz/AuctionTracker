package scrape;

import database.DatabaseQueries;
import database.DatabaseRecord;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bartek on 2017-07-20.
 */
public class CorrectAuctions {

    public CorrectAuctions() throws InterruptedException, ExecutionException, HttpException, IOException {

        DatabaseQueries databaseQueries = new DatabaseQueries();
        List<String> urlList = databaseQueries.getAuctionsToCorrectAuctionsFromWatchList();
        System.out.println("Auctions to correct: " + urlList);

        if(urlList.size() > 0) {
            for (int i = 0; i < urlList.size(); i++) {

                String url = urlList.get(i);
                ScrapeAuction scrapeAuction = new ScrapeAuction(url, 1);

            }
        } else {
            System.out.println("No auctions to correct!");
        }

    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, HttpException, IOException {
        CorrectAuctions correctAuctions = new CorrectAuctions();
    }
}
