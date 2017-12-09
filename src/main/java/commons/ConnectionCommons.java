package commons;

import database.DatabaseQueries;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.protocol.HttpRequestExecutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by bartek on 2017-08-09.
 */
public class ConnectionCommons {

    private HttpGet request;
    private BasicHttpClientConnectionManager basicConnManager;
    private String url;
    private ScrapeCommons scrapeCommons;
    private boolean auctionTitleSet;
    private String auctionTitle;
    private DatabaseQueries databaseQueries;


    public ConnectionCommons() {

    }

    public boolean getClosableResponse(String host, String url) throws ExecutionException, InterruptedException, HttpException {

        this.url = url;
        databaseQueries = new DatabaseQueries();
        scrapeCommons = new ScrapeCommons();

        String userAgent = scrapeCommons.getRandomUserAgent();

        basicConnManager =
                new BasicHttpClientConnectionManager();
        HttpClientContext context = HttpClientContext.create();

        // low level
        HttpRoute route = new HttpRoute(new HttpHost(host, 80));
        ConnectionRequest connRequest = basicConnManager.requestConnection(route, null);
        HttpClientConnection conn = null;
        try {
            conn = connRequest.get(10, TimeUnit.SECONDS);
        } catch (ConnectionPoolTimeoutException e) {
            e.printStackTrace();
        }
        try {
            basicConnManager.connect(conn, route, 1000, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            basicConnManager.routeComplete(conn, route, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpRequestExecutor exeRequest = new HttpRequestExecutor();
        context.setTargetHost((new HttpHost(host, 80)));
        request = new HttpGet("http://" + url);
        // set user agent
        request.setHeader("User-Agent", userAgent);
        try {
            exeRequest.execute(request, conn, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("User agent: " + userAgent);

        basicConnManager.releaseConnection(conn, null, 1, TimeUnit.SECONDS);

        request = new HttpGet("http://" + url);

        boolean urlExists = databaseQueries.urlExistsInWatchList(url);

        boolean dbCreated;
        if(!urlExists) {
            dbCreated = databaseQueries.createAuction(url);
        } else {
            dbCreated = databaseQueries.updateAuction(url);
        }

        return dbCreated;

    }

    public void getHTML() throws IOException {
        // high level
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(basicConnManager)
                .build();
        CloseableHttpResponse closeableHttpResponse = client.execute(request);
        System.out.println("Closeable response: " + closeableHttpResponse);

        HttpEntity entity = closeableHttpResponse.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                // do something useful
                Document doc = Jsoup.parse(instream, "UTF-8", "");
                Element bodyElement = doc.body();

                // Set auction title
                Element titleElement = bodyElement.getElementsByTag("h2").get(0);
                auctionTitle = titleElement.ownText();
                auctionTitle.replace("'", "\"");

                // Check if auction ended
                Elements counters = bodyElement.getElementsByAttributeValueContaining("class", "product-counter");
                Element counter = counters.get(0);
                System.out.println(counter);
                String counterValue = counter.ownText();
                System.out.println("Counter value: " + counterValue);

                // If auction ended, do following
                // OBS!! Save information one last time
                if (counterValue.contains("Zak")) {
                    System.out.println("AUCTION ENDED.. SAVING DATA TO DB");

                    // Insert winning auction details in db

                    Element winnerElement = bodyElement.getElementsByClass("winning-bidder").get(0);
                    String winnerName = winnerElement.ownText();
                    System.out.println("Winner name: " + winnerName);

                    Elements auctionDetailsElement = bodyElement.getElementsByClass("product-calculation");

                    String acProductPrice = auctionDetailsElement.get(0).child(1).ownText();
                    String productPrice = acProductPrice.substring(0, acProductPrice.length() - 2).replace(" ", "");
                    System.out.println("Price: " + productPrice);
                    float floatProductPrice = Float.parseFloat(productPrice);
                    System.out.println("Float price: " + floatProductPrice);

                    String acWinnerBidsAmount = auctionDetailsElement.get(0).child(2).ownText();
                    String winnerBidsAmount = acWinnerBidsAmount.replaceAll("\\D+", "");
                    System.out.println("Winner bids amount: " + winnerBidsAmount);
                    int intWinnerBidsAmount = Integer.parseInt(winnerBidsAmount);

                    String acWinnerBidsCost = auctionDetailsElement.get(0).child(3).ownText();
                    String winnerBidsCost = acWinnerBidsCost.substring(0, acWinnerBidsCost.length() - 2).replace(" ", "");
                    System.out.println("Winner bids cost: " + winnerBidsCost);
                    float floatwinnerBidsCost = Float.parseFloat(winnerBidsCost);
                    System.out.println("Float price: " + floatwinnerBidsCost);

                    String acTotalBids = auctionDetailsElement.get(0).child(5).ownText();
                    String totalBids = acTotalBids.replaceAll("\\D+", "");
                    System.out.println("Total bids: " + totalBids);
                    int intTotalBids = Integer.parseInt(totalBids);

                    float floatWinnerCostPerBid = floatwinnerBidsCost / intWinnerBidsAmount;
                    System.out.println("Winner cost per bid: " + floatWinnerCostPerBid);

                    // Get date
                    String currentTime = scrapeCommons.currentTime();

                    // Update db with date
                    databaseQueries.updateAuctionDetails(url, auctionTitle, winnerName, floatProductPrice, floatwinnerBidsCost, intWinnerBidsAmount, floatWinnerCostPerBid, intTotalBids, currentTime, 0, 1);
                    // Update Watchlist end_time and in_process = 0 (if on watchlist)
                    databaseQueries.updateAuctionWatchListStatus(url, currentTime, 0);

                    // Cancel task, purge and cancel timer
                    // *** REMOVED ***

                    System.out.println("AUCTION HAS ENDED .. QUIT .." + url);

                } else {
                    System.out.println("AUCTION HAS NOT! ENDED .. RETRY LATER .." + url);
                }
            } finally {
                instream.close();
                closeableHttpResponse.close();
            }
        }
    }
}
