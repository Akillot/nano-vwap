package com.zozulia.nanovwap.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VwapCalculatorTest {

    private final VwapCalculator vwapCalculator = new VwapCalculator();

    @Test
    void calculatesVwapForSeveralTrades(){
        vwapCalculator.add(1, 2);
        vwapCalculator.add(2, 4);

        double result = vwapCalculator.add(3, 5);
        assertEquals(25.0 / 11, result, 1e-6);

    }

    @Test
    void calculatesVwapForSeveralTradesZeroTotalVolStart(){
        vwapCalculator.add(1, 0);
        vwapCalculator.add(2, 4);

        double result = vwapCalculator.add(3, 5);
        assertEquals(23.0 / 9, result, 1e-6);
    }

    @Test
    void calculatesVwapForSingleTradeZeroTotalVol(){
        double result = vwapCalculator.add(1, 0);
        assertEquals(Double.NaN, result);
    }

    @Test
    void calculatesVwapForSeveralTradesFiltration(){
        vwapCalculator.add(100, 10);
        vwapCalculator.add(50, -5);

        double result = vwapCalculator.add(1, 0);
        assertEquals(100.0, result, 1e-9);
    }
}
