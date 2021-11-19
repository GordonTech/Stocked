package com.main.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StockRetriever {

    private static final String apiAuthKey = "G1XITBUIDUPZ91TG";


    public StockRetriever() {}

    public static List<Object[]> getAlphaVantageData(String symbol, TimeSeriesIntervals interval) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        List<Object[]> highStockList = new ArrayList<>();

        String getURL = constructGetURL(interval, symbol, "full");

        System.out.println(getURL);
        JsonNode firstNode = restTemplate.getForObject(getURL, JsonNode.class);

        //maps "Meta data" node and "Time Series" data
        if (firstNode != null) {
            Iterator<Map.Entry<String, JsonNode>> iterator = firstNode.fields();
            assert iterator.next().getKey().matches("Time Series (Daily)|Weekly Time Series|Monthly Time Series");

            Map.Entry<String, JsonNode> e = iterator.next();

            while (iterator.hasNext()) {
                //Node containing all the Time Series dates and value
                JsonNode secondNode = e.getValue();
                //Maps dates as keys and data values as Nodes
                Iterator<Map.Entry<String, JsonNode>> timeSeriesDataIterator = secondNode.fields();
                while (timeSeriesDataIterator.hasNext()) {
                    Map.Entry<String, JsonNode> timeSeriesInstance = timeSeriesDataIterator.next();

                    //ISO_LOCAL_DATE formats any YYYY-MM-DD value to ensure dates in
                    //other calendar systems are correctly converted
                    LocalDate timeStamp = LocalDate.parse(timeSeriesInstance.getKey(), DateTimeFormatter.ISO_LOCAL_DATE);

                    String open = String.valueOf(timeSeriesInstance.getValue().get("1. open")).replace("\"", "");
                    String high = String.valueOf(timeSeriesInstance.getValue().get("2. high")).replace("\"", "");
                    String low = String.valueOf(timeSeriesInstance.getValue().get("3. low")).replace("\"", "");
                    String close = String.valueOf(timeSeriesInstance.getValue().get("4. close")).replace("\"", "");
                    String volume = String.valueOf(timeSeriesInstance.getValue().get("5. volume")).replace("\"", "");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dt = sdf.parse(timeStamp.toString());
                    Long epoch = dt.getTime();
                    BigInteger epochBI = new BigInteger(String.valueOf(epoch));
                    BigDecimal openBD = new BigDecimal(open);
                    BigDecimal highBD = new BigDecimal(high);
                    BigDecimal lowBD = new BigDecimal(low);
                    BigDecimal closeBD = new BigDecimal(close);
                    BigDecimal volumeBD = new BigDecimal(volume);

                    highStockList.add(new Object[]{
                            epochBI,
                            openBD,
                            highBD,
                            lowBD,
                            closeBD,
                            volumeBD
                    });

                }
            }
            Collections.reverse(highStockList);
            return highStockList;
        } else {
            throw new NullPointerException("API call returned no data");
        }
    }


    public static String constructGetURL(TimeSeriesIntervals interval, String symbol, String outputLength) {
        return String.format("https://www.alphavantage.co/query?function=$%s&symbol=%s&apikey=%s&outputsize=%s", interval, symbol, apiAuthKey, outputLength);
    }
}
