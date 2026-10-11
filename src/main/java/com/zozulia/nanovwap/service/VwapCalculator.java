package com.zozulia.nanovwap.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class VwapCalculator {

    private double sumPriceVolume = 0.0;
    private double totalVolume = 0.0;
    private final ZoneId zoneID;
    private LocalDate sessionDate= null;

    public double add(double price, double size, Instant tradeTime) {
        LocalDate tradeDate = tradeTime.atZone(zoneID).toLocalDate();

        if (sessionDate == null) {
            sessionDate = tradeDate;
        }
        else if(tradeDate.isAfter(sessionDate)) {
            reset();
            sessionDate = tradeDate;
        }

        if (price > 0 && size > 0) {
            sumPriceVolume += price * size;
            totalVolume += size;
        }
        if (totalVolume == 0) return Double.NaN;
        return sumPriceVolume / totalVolume;
    }

    public void reset(){
        sumPriceVolume = 0.0;
        totalVolume = 0.0;
    }

    public VwapCalculator(ZoneId zoneID) {
        this.zoneID = zoneID;
    }
}
