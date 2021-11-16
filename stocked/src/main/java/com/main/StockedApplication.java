package com.main;

import com.main.data.StockData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class StockedApplication {

	public static void main(String[] args) {
		StockData example = new StockData(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);
		SpringApplication.run(StockedApplication.class, args);
	}


}
