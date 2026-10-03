package com.zozulia.nanovwap.service;

public class VwapCalculator {

    private double sumPriceVolume = 0.0;
    private double totalVolume = 0.0;

    public double add(double price, double size) {
        if (price > 0 && size > 0) {
            sumPriceVolume += price * size;
            totalVolume += size;
        }
        if (totalVolume == 0) return Double.NaN;
        return sumPriceVolume / totalVolume;
    }
}
