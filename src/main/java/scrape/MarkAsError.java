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
public class MarkAsError {

    public MarkAsError() throws InterruptedException, ExecutionException, HttpException, IOException {

        DatabaseQueries databaseQueries = new DatabaseQueries();
        databaseQueries.markAsErrorInWatchlist();

    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, HttpException, IOException {
        MarkAsError markAsError = new MarkAsError();
    }
}
