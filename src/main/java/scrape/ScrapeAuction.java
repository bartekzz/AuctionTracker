package scrape;

import commons.ScrapeCommons;
import database.DatabaseQueries;
import org.apache.http.*;
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
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrapeAuction {

    private DatabaseQueries databaseQueries;
    private String url;
    private HttpGet request;
    private BasicHttpClientConnectionManager basicConnManager;
    private boolean auctionTitleSet;
    private String auctionTitle;
    private ScrapeCommons scrapeCommons;
    private Timer timer;
    private TimerTask timerTask;
    private int error;

    public ScrapeAuction(String url, int error) throws InterruptedException, ExecutionException, HttpException, IOException {

        // Set instance var error
        this.error = error;
        // Set url
        this.url = url;

        System.out.println("Scraping: " + url);
        databaseQueries = new DatabaseQueries();
        scrapeCommons = new ScrapeCommons();

        boolean dbCreated = getClosableResponse("gambid.com", url);

        if(dbCreated) {
            setTimer(20);
        } else {
            System.out.println("Db not created!");
        }

    }

    public void setTimer(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    runScrape();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1 * seconds * 1000);
    }

    public void runScrape() throws InterruptedException, ExecutionException, HttpException {

        try {
            //getClosableResponse();
            getHTML();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean getClosableResponse(String host, String url) throws ExecutionException, InterruptedException, HttpException {

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

        //String currentTime = scrapeCommons.currentTime();

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

                // Set auction title if not set
                if(!auctionTitleSet) {
                    Element titleElement = bodyElement.getElementsByTag("h2").get(0);
                    auctionTitle = titleElement.ownText();
                    auctionTitle.replace("'", "\"");
                    //databaseQueries.setAuctionTitle(url, auctionTitle);
                    auctionTitleSet = true;
                }

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
                    String winnerBidsAmount = acWinnerBidsAmount.replaceAll("\\D+","");
                    System.out.println("Winner bids amount: " + winnerBidsAmount);
                    int intWinnerBidsAmount = Integer.parseInt(winnerBidsAmount);

                    String acWinnerBidsCost = auctionDetailsElement.get(0).child(3).ownText();
                    String winnerBidsCost = acWinnerBidsCost.substring(0, acWinnerBidsCost.length() - 2).replace(" ", "");
                    System.out.println("Winner bids cost: " + winnerBidsCost);
                    float floatwinnerBidsCost = Float.parseFloat(winnerBidsCost);
                    System.out.println("Float price: " + floatwinnerBidsCost);

                    String acTotalBids = auctionDetailsElement.get(0).child(5).ownText();
                    String totalBids = acTotalBids.replaceAll("\\D+","");
                    System.out.println("Total bids: " + totalBids);
                    int intTotalBids = Integer.parseInt(totalBids);

                    float floatWinnerCostPerBid = floatwinnerBidsCost/intWinnerBidsAmount;
                    System.out.println("Winner cost per bid: " + floatWinnerCostPerBid);

                    // Get date
                    String currentTime = scrapeCommons.currentTime();

                    // Update db with date
                    databaseQueries.updateAuctionDetails(url, auctionTitle, winnerName, floatProductPrice, floatwinnerBidsCost, intWinnerBidsAmount, floatWinnerCostPerBid, intTotalBids, currentTime, 0, error);
                    // Update Watchlist end_time and in_process = 0 (if on watchlist)
                    databaseQueries.updateAuctionWatchListStatus(url, currentTime, 0);

                    // Cancel task, purge and cancel timer
                    timerTask.cancel();
                    timer.purge();
                    timer.cancel();

                    System.out.println("AUCTION QUITTING .." + url);

                } else {
                    // If auction has not ended, get auction data
                    Elements lastBiddersElements = bodyElement.getElementsByClass("last-bidders");
                    Element lastBidderElement = lastBiddersElements.get(0);
                    Elements uniqBiddingElements = lastBidderElement.getElementsByTag("li");

                    if(uniqBiddingElements != null) {
                        for (Element uniqBiddingElement : uniqBiddingElements) {
                            String uniqBiddingElementAttr = uniqBiddingElement.attr("uniq");
                            System.out.println("Bid number: " + uniqBiddingElementAttr);

                            Element childNameElement = uniqBiddingElement.child(0);
                            String childNameText = childNameElement.ownText();
                            System.out.println("Bidder's name: " + childNameText);

                            Element childBidElement = uniqBiddingElement.child(1);
                            String childBidText = childBidElement.ownText();
                            System.out.println("Bid amount: " + childBidText);

                            Pattern p = Pattern.compile("([^\\s]+)");
                            Matcher m = p.matcher(childNameText);;
                            String childNameTextUniq = null;
                            if (m.find( )) {
                                System.out.println("Bidder's uniq name: " + m.group(1) );
                                childNameTextUniq = m.group(1);
                            }else {
                                System.out.println("No unique name");
                            }

                            // Save to db if unique
                            if (uniqBiddingElementAttr != null && childNameText != null && childBidText != null) {
                                databaseQueries.insertAuctionData(url, uniqBiddingElementAttr, childNameText, childBidText.substring(0, childBidText.length() - 2), childNameTextUniq);
                            }

                        }
                    }
                }

            } finally {
                instream.close();
                closeableHttpResponse.close();
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, HttpException, IOException {
        //ScrapeAuction scrapeAuction = new ScrapeAuction();

    }
}