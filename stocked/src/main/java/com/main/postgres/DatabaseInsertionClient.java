package com.main.postgres;

import com.main.api.StockRetriever;
import com.main.api.TimeSeriesIntervals;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.main.api.StockRetriever.getAlphaVantageData;
import static com.main.api.TimeSeriesIntervals.*;
import static com.main.postgres.ConnectionClient.createConnection;
import static java.lang.Thread.sleep;
import com.main.io.*;

public class DatabaseInsertionClient {

    public static void insertSymbolKey(long id, String symbol) {

        String SQL = "INSERT INTO stocksymbols(symbol, id) VALUES(?,?)";

        try (Connection conn = createConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, symbol);
            pstmt.setLong(2, id);

            pstmt.executeUpdate();
            System.out.println("Symbol: " + symbol + " added with id: " + id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertStockData() {
        List<String> symbolList = FileClient.readTextFile("./src/main/resources/static/NASDAQ Symbols");
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            for (int id = 0; id < symbolList.size(); id++) {
                String symbol = symbolList.get(id);
                try {
                    insertSymbolKey(id, symbol);
                    List<Object[]> arr = getAlphaVantageData(symbol, TIME_SERIES_DAILY);
                    if (arr.size() != 0) {
                        insertPostgresData(id, arr, TIME_SERIES_DAILY);
                        arr = getAlphaVantageData(symbol, TIME_SERIES_WEEKLY);
                        insertPostgresData(id, arr, TIME_SERIES_WEEKLY);
                        arr = getAlphaVantageData(symbol, TIME_SERIES_MONTHLY);
                        insertPostgresData(id, arr, TIME_SERIES_MONTHLY);
                    } else {
                        dropStockSymbol(id);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 6, TimeUnit.SECONDS);
    }

    public static void insertPostgresData(long id, List<Object[]> dataList, TimeSeriesIntervals interval) {
        String SQL = switch (interval) {
            case TIME_SERIES_DAILY -> "INSERT INTO dailydata(id, date, open, high, low, close, volume) VALUES(?,?,?,?,?,?,?)";
            case TIME_SERIES_WEEKLY -> "INSERT INTO weeklydata(id, date, open, high, low, close, volume) VALUES(?,?,?,?,?,?,?)";
            case TIME_SERIES_MONTHLY -> "INSERT INTO monthlydata(id, date, open, high, low, close, volume) VALUES(?,?,?,?,?,?,?)";
        };

        try (Connection conn = createConnection();
            PreparedStatement prepareStatement = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)){
                for (Object[] data : dataList) {
                    prepareStatement.setLong(1, id);
                    prepareStatement.setObject(2, data[0]);
                    prepareStatement.setObject(3, data[1]);
                    prepareStatement.setObject(4, data[2]);
                    prepareStatement.setObject(5, data[3]);
                    prepareStatement.setObject(6, data[4]);
                    prepareStatement.setObject(7, data[5]);
                    prepareStatement.addBatch();
                    prepareStatement.executeBatch();
                    System.out.println("Input length: " + dataList.size());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void dropStockSymbol(long id) {
        String SQL = "DELETE FROM stocksymbols WHERE id = " + id;
        try (Connection conn = createConnection();
             PreparedStatement prepareStatement
                     = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS))
        {
            prepareStatement.executeUpdate();
            System.out.println("Dropped stock with id: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
