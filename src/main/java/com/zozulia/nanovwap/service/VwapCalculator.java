package com.zozulia.nanovwap.service;

import java.time.Instant;
import java.time.LocalDate;

public class VwapCalculator {

    private double sumPriceVolume = 0.0;
    private double totalVolume = 0.0;
    private final TradingSession tradingSession;
    private LocalDate sessionDate = null;

    public double add(double price, double size, Instant tradeTime) {
        LocalDate tradeDate = tradeTime.atZone(tradingSession.zone()).toLocalDate();

        if (sessionDate == null) {
            sessionDate = tradeDate;
        }
        else if(tradeDate.isAfter(sessionDate)) {
            reset();
            sessionDate = tradeDate;
        }
        else if(tradeDate.isBefore(sessionDate)) {
            return getVwap();
        }

        if (price > 0 && size > 0) {
            sumPriceVolume += price * size;
            totalVolume += size;
        }
        return getVwap();
    }

    public void reset(){
        sumPriceVolume = 0.0;
        totalVolume = 0.0;
    }

    public VwapCalculator(TradingSession tradingSession) {
        this.tradingSession = tradingSession;
    }

    public double getVwap(){
        if (totalVolume == 0) return Double.NaN;
        return sumPriceVolume / totalVolume;
    }
}
