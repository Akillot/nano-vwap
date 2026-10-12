package com.zozulia.nanovwap.service;

import java.time.LocalTime;
import java.time.ZoneId;

public record TradingSession(ZoneId zone, LocalTime open, LocalTime close) {
}
