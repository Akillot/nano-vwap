package com.zozulia.nanovwap.websocket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Configuration
public class AlpacaWebSocketConfig {
    private final static String STOCK_URL = "wss://stream.data.alpaca.markets/v2/iex";
    private final static String CRYPTO_URL = "wss://stream.data.alpaca.markets/v1beta3/crypto/us";

    private final List<String> stockTickers = new ArrayList<>(List.of("AAPL", "MSFT", "GOOGL", "META", "NVDA", "AMZN", "TSLA", "PLTR", "AVGO"));
    private final List<String> cryptoTickers = new ArrayList<>(List.of("BTC/USD", "ETH/USD", "LTC/USD", "SOL/USD", "AVAX/USD", "XRP/USD"));

    @Bean
    @Profile("!test")
    public CommandLineRunner commandLineRunner(ObjectMapper objectMapper, @Value("${alpaca.api.key}") String apiKey, @Value("${alpaca.api.secret}") String apiSecret) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            String url, currentTicker;

            System.out.println("\nStock Tickers:");
            for (String ticker : stockTickers) System.out.print(ticker + " ");
            System.out.println("\n\nCrypto Tickers:");
            for (String ticker : cryptoTickers) System.out.print(ticker + " ");

            while (true) {
                System.out.print("\nPlease pick the ticker: ");
                currentTicker = scanner.nextLine().toUpperCase().trim();

                if (stockTickers.contains(currentTicker)) {
                    url = STOCK_URL;
                    break;
                }
                else if (cryptoTickers.contains(currentTicker)) {
                    url = CRYPTO_URL;
                    break;
                }
                else System.out.println("Invalid ticker: " + currentTicker);
            }

            System.out.println("Ticker has been picked: " + currentTicker + ". Awaiting signal...");

            var client = new StandardWebSocketClient();
            var handler = new AlpacaWebSocketHandler(objectMapper, currentTicker, apiKey, apiSecret);
            client.execute(handler, url);
        };
    }
}