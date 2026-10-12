package com.zozulia.nanovwap.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VwapCalculatorTest {

    private final static ZoneId UTC_ZONE = ZoneId.of("UTC");
    private final static TradingSession CRYPTO_TRADING_SESSION = new TradingSession(UTC_ZONE, LocalTime.MIN, LocalTime.MAX);
    private static final Instant SOME_TIME = Instant.parse("2026-10-07T10:00:00Z");

    private final VwapCalculator vwapCalculator = new VwapCalculator(CRYPTO_TRADING_SESSION);

    @Test
    void calculatesVwapForSeveralTrades(){
        vwapCalculator.add(1, 2, SOME_TIME);
        vwapCalculator.add(2, 4, SOME_TIME);

        double result = vwapCalculator.add(3, 5, SOME_TIME);
        assertEquals(25.0 / 11, result, 1e-6);

    }

    @Test
    void calculatesVwapForSeveralTradesZeroTotalVolStart(){
        vwapCalculator.add(1, 0, SOME_TIME);
        vwapCalculator.add(2, 4, SOME_TIME);

        double result = vwapCalculator.add(3, 5, SOME_TIME);
        assertEquals(23.0 / 9, result, 1e-6);
    }

    @Test
    void calculatesVwapForSingleTradeZeroTotalVol(){
        double result = vwapCalculator.add(1, 0, SOME_TIME);
        assertEquals(Double.NaN, result);
    }

    @Test
    void calculatesVwapForSeveralTradesFiltration(){
        vwapCalculator.add(100, 10, SOME_TIME);
        vwapCalculator.add(50, -5, SOME_TIME);

        double result = vwapCalculator.add(1, 0, SOME_TIME);
        assertEquals(100.0, result, 1e-9);
    }

    @Test
    void resetVwapSession(){
        vwapCalculator.add(100, 12, SOME_TIME);
        vwapCalculator.add(50, 22, SOME_TIME);
        vwapCalculator.reset();

        double result = vwapCalculator.add(100, 10, SOME_TIME);
        assertEquals(100.0,result,1e-9);
    }

    // One day - one session
    @Test
    void resetVwapBetweenSessions(){
        vwapCalculator.add(100, 10, Instant.parse("2026-10-07T23:00:00Z"));
        vwapCalculator.add(200, 10, Instant.parse("2026-10-07T23:20:00Z"));
        vwapCalculator.add(50,10,Instant.parse("2026-10-08T00:20:00Z"));
        double result = vwapCalculator.add(70,10,Instant.parse("2026-10-08T00:25:00Z"));
        assertEquals(60.0,result,1e-9);
    }

    @Test
    void ignoresTradeFromPreviousSession(){
        vwapCalculator.add(100, 10, Instant.parse("2026-10-08T23:00:00Z"));
        double result = vwapCalculator.add(500, 10, Instant.parse("2026-10-07T23:00:00Z"));

        assertEquals(100.0,result,1e-9);
    }
}
