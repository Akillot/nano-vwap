package com.zozulia.nanovwap.websocket;

import com.zozulia.nanovwap.service.TradingSession;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.ObjectMapper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Configuration
public class AlpacaWebSocketConfig {
    private final static String STOCK_URL = "wss://stream.data.alpaca.markets/v2/iex";
    private final static String CRYPTO_URL = "wss://stream.data.alpaca.markets/v1beta3/crypto/us";

    private final static DecimalFormatSymbols US_SYMBOLS = new DecimalFormatSymbols(Locale.US);

    private final static DecimalFormat STOCK_FORMAT = new DecimalFormat("0.###", US_SYMBOLS);
    private final static DecimalFormat CRYPTO_FORMAT = new DecimalFormat("0.########", US_SYMBOLS);

    private final static ZoneId STOCK_NY_ZONE = ZoneId.of("America/New_York");
    private final static ZoneId UTC_ZONE = ZoneId.of("UTC");

    private final static TradingSession STOCK_NY_TRADING_SESSION = new TradingSession(STOCK_NY_ZONE, LocalTime.parse("09:30"), LocalTime.parse("16:00"));
    private final static TradingSession CRYPTO_TRADING_SESSION = new TradingSession(UTC_ZONE, LocalTime.MIN, LocalTime.MAX);

    private final List<String> stockTickers = new ArrayList<>(List.of("AAPL", "MSFT", "GOOGL", "META", "NVDA", "AMZN", "TSLA", "PLTR", "AVGO"));
    private final List<String> cryptoTickers = new ArrayList<>(List.of("BTC/USD", "ETH/USD", "LTC/USD", "SOL/USD", "AVAX/USD", "XRP/USD"));

    @Bean
    @Profile("!test")
    public CommandLineRunner commandLineRunner(ObjectMapper objectMapper, @Value("${alpaca.api.key}") String apiKey, @Value("${alpaca.api.secret}") String apiSecret) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            String url, currentTicker;
            DecimalFormat format;
            TradingSession tradingSession;

            System.out.println("\nStock Tickers:");
            for (String ticker : stockTickers) System.out.print(ticker + " ");
            System.out.println("\n\nCrypto Tickers:");
            for (String ticker : cryptoTickers) System.out.print(ticker + " ");

            while (true) {
                System.out.print("\nPlease pick the ticker: ");
                currentTicker = scanner.nextLine().toUpperCase().trim();

                if (stockTickers.contains(currentTicker)) {
                    url = STOCK_URL;
                    format = STOCK_FORMAT;
                    tradingSession = STOCK_NY_TRADING_SESSION;
                    break;
                }
                else if (cryptoTickers.contains(currentTicker)) {
                    url = CRYPTO_URL;
                    format = CRYPTO_FORMAT;
                    tradingSession = CRYPTO_TRADING_SESSION;
                    break;
                }
                else System.out.println("Invalid ticker: " + currentTicker);
            }

            System.out.println("Ticker has been picked: " + currentTicker + ". Awaiting signal...");
            var handler = new AlpacaWebSocketHandler(objectMapper, currentTicker, apiKey, apiSecret, url, format, tradingSession);
            handler.connect();
        };
    }
}