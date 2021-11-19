package com.main.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;


public record StockData(BigDecimal date, BigDecimal open,
                         BigDecimal high,  BigDecimal low,
                         BigDecimal close, BigDecimal volume) {


    public StockData(BigDecimal date, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume) {
        this.date = date;
        this.open = open.setScale(4, RoundingMode.HALF_EVEN);
        this.high = high.setScale(4, RoundingMode.HALF_EVEN);
        this.low = low.setScale(4, RoundingMode.HALF_EVEN);
        this.close = close.setScale(4, RoundingMode.HALF_EVEN);
        this.volume = volume.setScale(4, RoundingMode.HALF_EVEN);
    }

    public String dateToString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(this.date);
    }

}
