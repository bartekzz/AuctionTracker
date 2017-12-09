package scrape;

import commons.ConnectionCommons;
import database.DatabaseQueries;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bartek on 2017-08-09.
 */
public class CheckPastAuctions {

    private DatabaseQueries databaseQueries;
    private ConnectionCommons connectionCommons;

    public CheckPastAuctions() {
        checkIfAuctionEnded();
    }

    public void checkIfAuctionEnded() {

        connectionCommons = new ConnectionCommons();

        databaseQueries = new DatabaseQueries();
        List<String> runningAuctions = databaseQueries.getRunningAuctions();

        boolean dbCreated = false;

        for(String runningAuction : runningAuctions) {
            try {
                dbCreated = connectionCommons.getClosableResponse("gambid.com", runningAuction);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            }
            if(dbCreated) {
                try {
                    connectionCommons.getHTML();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Db not created! (Check running auctions)");
            }
        }
    }

    public static void main(String[] args) {
        CheckPastAuctions checkPastAuctions = new CheckPastAuctions();
    }
}
