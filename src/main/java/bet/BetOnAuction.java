/*
package bet;

import commons.ScrapeCommons;
import database.DatabaseQueries;
import org.apache.commons.lang3.StringUtils;
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by bartek on 2017-08-12.
 */
/*
public class BetOnAuction {

    private HttpGet request;
    private BasicHttpClientConnectionManager basicConnManager;
    private String url;
    private ScrapeCommons scrapeCommons;
    private boolean auctionTitleSet;
    private String auctionTitle;
    private DatabaseQueries databaseQueries;

    public BetOnAuction() {

    }

    public void getClosableResponse(String host, String url) throws ExecutionException, InterruptedException, HttpException {

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

    }

    public void getLinks() throws IOException {
        // high level
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(basicConnManager)
                .build();
        CloseableHttpResponse closeableHttpResponse = client.execute(request);
        System.out.println("Closeable response: " + closeableHttpResponse);

        HttpEntity entity = closeableHttpResponse.getEntity();

        String userDir = System.getProperty("user.dir");
        System.out.println("User Dir: " + userDir);

        System.setProperty("webdriver.gecko.driver", userDir + "/" + ScrapeCommons.getGeckoDriver());
        FirefoxProfile ffp = new FirefoxProfile();
        ffp.setPreference("general.useragent.override", userAgent);
        WebDriver driver = new FirefoxDriver(ffp);
        driver.get("https://" + url);

        List<WebElement> auctionObserversJS = driver.findElements(By.className("auctionobserver"));

        // class = btn bidup

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                // do something useful
                Document doc = Jsoup.parse(instream, "UTF-8", "");
                org.jsoup.nodes.Element bodyElement = doc.body();

                Elements auctionObservers = bodyElement.getElementsByClass("auctionobserver");
                for (Element auctionObserver : auctionObservers) {
                    //System.out.println("AuctionObserver: " + auctionObserver);
                    String auctionLink = auctionObserver.child(1).child(0).child(0).attr("href");
                    System.out.println("Link: " + auctionLink);
                    String auctionTitle = auctionObserver.child(1).child(0).child(0).ownText();
                    System.out.println("Title: " + auctionTitle);

                    // Setting filter. Only save auction to watchlist if Apple-product
                    if(StringUtils.containsIgnoreCase(auctionTitle, "apple")) {

                        // Get auction start time with Selenium (cos it's javascripted)

                        for (WebElement auctionObserverJS : auctionObserversJS) {
                            WebElement titleJS = auctionObserverJS.findElement(By.className("title"));
                            WebElement anchor = titleJS.findElement(By.tagName("a"));
                            if (anchor.getAttribute("href").equals("https://sknerus.pl" + auctionLink)) {
                                WebElement auctionEndDate = auctionObserverJS.findElement(By.className("auctionenddate"));
                                String auctionStartsIn = auctionEndDate.getText();
                                System.out.println("Auction starts in: " + auctionStartsIn);
                                while(auctionStartsIn.contains("Auto")) {
                                    auctionStartsIn = auctionEndDate.getText();
                                    System.out.println("Auction starts in (retry): " + auctionStartsIn);
                                }

                                // Count seconds for auctionStartsIn, if higher than 10 seconds save to db-watchlist
                                int auctionStartTimeInSeconds = scrapeCommons.convertTimeStringToSeconds(auctionStartsIn);

                                if(auctionStartTimeInSeconds > 10) {

                                    List<Integer> timeList = scrapeCommons.convertTimeStringToList(auctionStartsIn);
                                    String auctionStartDate = scrapeCommons.addTimeToCurrentDate(timeList);
                                    System.out.println("Start date: " + auctionStartDate);
                                    System.out.println("***");

                                    String modifiedAuctionLink = "sknerus.pl" + auctionLink;

                                    // save auctionLink, auctionStartDate
                                    databaseQueries.addAuctionToWatchList(modifiedAuctionLink, auctionStartDate);
                                }
                            }
                        }
                    }

                }

            } finally {
                instream.close();
                closeableHttpResponse.close();
                driver.close();
            }
        }

    }
}

*/
