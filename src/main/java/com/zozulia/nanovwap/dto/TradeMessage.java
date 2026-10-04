package com.zozulia.nanovwap.dto;


import java.time.Instant;

// S - Symbol, p - price, s - size, t - time
public record TradeMessage(String S, double p, double s, Instant t) {
}
