package com.zozulia.nanovwap.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TradingSessionTest {

    private final static TradingSession STOCK_NY_TRADING_SESSION = new TradingSession(ZoneId.of("America/New_York"), LocalTime.parse("09:30"), LocalTime.parse("16:00"));

    @Test
    void closedOneSecondBeforeOpen(){
        assertFalse(STOCK_NY_TRADING_SESSION.isOpenAt(Instant.parse("2026-10-07T13:29:59Z")));
    }

    @Test
    void  openExactlyAtOpen(){
        assertTrue(STOCK_NY_TRADING_SESSION.isOpenAt(Instant.parse("2026-10-07T13:30:00Z")));
    }

    @Test
    void closedExactlyAtClose() {
        assertFalse(STOCK_NY_TRADING_SESSION.isOpenAt(Instant.parse("2026-10-07T20:00:00Z")));
    }
}
