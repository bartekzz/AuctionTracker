package database;

import commons.ScrapeCommons;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;


// JDK 1.7 and above
public class DatabaseQueries {    // Save as "JdbcUpdateTest.java"

    static String auctionList = DatabaseFields.AUCTION_LIST;
    static String auctionData = DatabaseFields.AUCTION_DATA;
    static String auctionWatchList = DatabaseFields.AUCTION_WATCHLIST;

    public DatabaseQueries() {
        //insertRecords(144, 106, 1);
    }

    public static boolean urlExistsInWatchList(String url) {
        System.out.println("Checking if url exists in watch list ..");
        //DatabaseRecord databaseRecord;

        boolean urlExists = false;

        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Add a loop with OFFSET (if indexed = 0) for chosen record(s)
            // Issue a SELECT to check the changes
            String strSelect = "select * from " + auctionList + " where url = '" + url + "'";
            System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
            ResultSet rset = stmt.executeQuery(strSelect);

            ScrapeCommons scrapeCommons = new ScrapeCommons();

            while (rset.next()) {   // Move the cursor to the next row

                url = rset.getString("url");

                if(url != null) { // First number is hours i.e "12"

                    urlExists = true;
                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return urlExists;
    }

    public static void updateAuctionWatchListStatus(String url, String currentTime, int inProcess) {
        System.out.println("Updating auction's status from watch list ..");
        if(inProcess != 0 && inProcess != 1 ) {
            System.out.println("Invalid value. Must be 0 or 1.");
            System.exit(1);
        }
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // INSERT a partial record
            String sqlInsert = "update " + auctionWatchList + " set in_process = " + inProcess + ", end_time = '" + currentTime + "' where url = '" + url + "'";
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records updated.\n");

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean createAuction(String url) {
        System.out.println("Creating auction in Auction List..");

        boolean created = false;
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // SELECT record from Auction Watch List
            String strSelect = "select start_time from " + auctionWatchList + " where url = '" + url + "'";
            System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
            ResultSet rset = stmt.executeQuery(strSelect);
            String startTime = "";
            while (rset.next()) {
                startTime = rset.getString("start_time");
            }

            // INSERT a partial record
            String sqlInsert = "insert ignore into " + auctionList + " (url, start_time) values ('" + url + "', '" + startTime + "')";
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records inserted.\n");

            created = true;

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return created;
    }

    public static boolean updateAuction(String url) {
        System.out.println("Updating auction in Auction List..");

        boolean created = false;
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {

            // SELECT record from Auction Watch List
            String strSelect = "select start_time from " + auctionWatchList + " where url = '" + url + "'";
            System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
            ResultSet rset = stmt.executeQuery(strSelect);
            String startTime = "";
            while (rset.next()) {
                startTime = rset.getString("start_time");
            }

            // INSERT a partial record
            String sqlInsert = "update " + auctionList + " set start_time = '" + startTime + "' where url = '" + url + "'";
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records inserted.\n");

            created = true;

        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return created;
    }

    public static void insertAuctionData(String url, String bidId, String bidName, String bidAmount, String bidNameUniq) {
        System.out.println("Inserting auction data ..");
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {

            // INSERT a partial record
            String sqlInsert = "insert into " + auctionData + " (url, bid_id, bid_name, bid_amount, bid_name_uniq) values ('" + url + "', '" + bidId + "', '" + bidName + "', '" + bidAmount + "', '" + bidNameUniq + "')";
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records inserted.\n");

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void updateAuctionDetails(String url, String auctionTitle, String winnerName, float floatProductPrice, float floatwinnerBidsCost,
                                            int intWinnerBidsAmount, float floatWinnerCostPerBid, int intTotalBids, String currentTime, int inProcess, int error) {
        System.out.println("Updating auction details ..");
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {

            // INSERT a partial record
            String sqlInsert = "update " + auctionList + " set auction = '" + auctionTitle + "', winner = '" + winnerName + "', price = '" + floatProductPrice + "', winner_cost = '" + floatwinnerBidsCost + "', winner_bids = '" + intWinnerBidsAmount + "', cost_per_bid = '" + floatWinnerCostPerBid + "', total_bids = '" + intTotalBids + "', end_time = '" + currentTime + "', in_process = '" + inProcess + "', error = '" + error + "' where url = '" + url +"'";
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records inserted.\n");

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void addAuctionToWatchList(String url, String auctionStartDate) {
        System.out.println("Adding auction to watch list ..");
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {

            // INSERT a partial record
            String sqlInsert = "insert ignore into " + auctionWatchList + " (url, start_time) values ('" + url + "', '" + auctionStartDate + "')";
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records inserted.\n");

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Do something about limit if action time/date has passed
    public static List<DatabaseRecord> getAuctionsFromWatchList(int limit) {
        System.out.println("Getting auctions from watch list ..");
        List<DatabaseRecord> urlList =  new ArrayList<>();
        //DatabaseRecord databaseRecord;

        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Add a loop with OFFSET (if indexed = 0) for chosen record(s)
            // Issue a SELECT to check the changes
            String strSelect = "select * from " + auctionWatchList + " where in_process = 0 and end_time is null and error is null";
            System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
            ResultSet rset = stmt.executeQuery(strSelect);

            int id;
            String url = "";
            String startTime = "";

            ScrapeCommons scrapeCommons = new ScrapeCommons();

            int recordsAdded = 0;

            while (rset.next() && recordsAdded < limit) {   // Move the cursor to the next row

                id = rset.getInt("id");
                url = rset.getString("url");

                startTime = rset.getString("start_time");
                ZonedDateTime startTimeAsDate = scrapeCommons.convertTimeStringToDate(startTime);
                long timeDifference = ZonedDateTime.now().until(startTimeAsDate, ChronoUnit.SECONDS);
                System.out.println("Time difference: " + timeDifference);

                DatabaseRecord databaseRecord = new DatabaseRecord(url, timeDifference);

                if(timeDifference < 24*60*60 && timeDifference > 0) { // First number is hours i.e "12"

                    urlList.add(databaseRecord);
                    setAuctionOnWatchListToInProcess(id, 1);
                    recordsAdded++;
                }

                //System.out.println(urlList);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return urlList;
    }

    public static void setAuctionOnWatchListToInProcess(int id, int inProcess) {
        System.out.println("Setting auction on watch list to in-process ..");
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {

            // INSERT a partial record
            String sqlInsert = "update " + auctionWatchList + " set in_process = " + inProcess + " where id = " + id;
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records inserted.\n");

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static List<String> getAuctionsToCorrectAuctionsFromWatchList() {
        System.out.println("Getting auction to correct from watch list ..");
        List<String> urlList =  new ArrayList<>();

        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Add a loop with OFFSET (if indexed = 0) for chosen record(s)
            // Issue a SELECT to check the changes
            String strSelect = "select * from " + auctionWatchList + " where in_process = 1 and end_time is null and error = 1";
            System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
            ResultSet rset = stmt.executeQuery(strSelect);

            //int id;
            String url = "";

            while (rset.next()) {   // Move the cursor to the next row

                //id = rset.getInt("id");
                url = rset.getString("url");
                urlList.add(url);

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return urlList;
    }

    public static void markAsErrorInWatchlist() {
        System.out.println("Setting auction on watch list to in-process ..");
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {

            // INSERT a partial record
            String sqlInsert = "update " + auctionWatchList + " set in_process = 1, error = 1 where end_time is null";
            System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
            int countInserted = stmt.executeUpdate(sqlInsert);
            System.out.println(countInserted + " records inserted.\n");

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static List<String> getRunningAuctions() {
        System.out.println("Getting running/past auctions from watch list ..");
        List<String> urlList =  new ArrayList<>();

        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sknerus?useSSL=false&serverTimezone=UTC", "root", "Bartek82"); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Add a loop with OFFSET (if indexed = 0) for chosen record(s)
            // Issue a SELECT to check the changes
            String strSelect = "select * from " + auctionWatchList + " where end_time is null";
            System.out.println("The SQL query is: " + strSelect);  // Echo For debugging
            ResultSet rset = stmt.executeQuery(strSelect);

            String url = "";
            String startTime = "";

            ScrapeCommons scrapeCommons = new ScrapeCommons();

            while (rset.next()) {   // Move the cursor to the next row

                url = rset.getString("url");

                startTime = rset.getString("start_time");
                ZonedDateTime startTimeAsDate = scrapeCommons.convertTimeStringToDate(startTime);
                long timeDifference = ZonedDateTime.now().until(startTimeAsDate, ChronoUnit.SECONDS);
                System.out.println("Url: " + url + ", Time difference: " + timeDifference);

                if(timeDifference < 0) { // Auctions start time has past
                    urlList.add(url);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return urlList;
    }


    public static void main(String[] args) {
        //DatabaseQueries conn = new DatabaseQueries();
    }
}
