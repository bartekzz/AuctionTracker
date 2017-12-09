package database;

/**
 * Created by bartek on 2017-06-05.
 */
public class DatabaseRecord {

    private long auctionStartInSeconds;
    private String url;

    DatabaseRecord(String url, long auctionStartInSeconds) {
        this.auctionStartInSeconds = auctionStartInSeconds;
        this.url = url;
    }

    public long getauctionStartInSeconds() {
        return auctionStartInSeconds;
    }

    public String getUrl() {
        return url;
    }
}
