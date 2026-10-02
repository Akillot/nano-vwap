package com.zozulia.nanovwap.websocket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Configuration
public class AlpacaWebSocketConfig {

    private final List<String> tickers = new ArrayList<>(List.of("AAPL", "MSFT", "GOOGL", "META", "NVDA", "AMZN", "TSLA", "PLTR", "AVGO"));

    @Bean
    public CommandLineRunner commandLineRunner(ObjectMapper objectMapper, @Value("${alpaca.api.key}") String apiKey, @Value("${alpaca.api.secret}") String apiSecret) {
        return args ->{
            Scanner scanner = new Scanner(System.in);

            for (String ticker : tickers) System.out.print(ticker + " ");

            System.out.print("\nPlease pick the ticker: ");
            String currentTicker = scanner.nextLine().toUpperCase().trim();
            System.out.println("Ticker has been picked: " + currentTicker + ". Awaiting signal...");

            var client = new StandardWebSocketClient();
            var handler = new AlpacaWebSocketHandler(objectMapper, currentTicker, apiKey, apiSecret);
            String url = "wss://stream.data.alpaca.markets/v1beta3/crypto/us"; // crypto
            //String url = "wss://stream.data.alpaca.markets/v2/iex"; // stocks
            client.execute(handler, url);
        };
    }
}