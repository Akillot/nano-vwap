package com.zozulia.nanovwap.service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public record TradingSession(ZoneId zone, LocalTime open, LocalTime close) {

    public boolean isOpenAt(Instant moment){
        LocalTime time = moment.atZone(zone).toLocalTime();
        return !time.isBefore(open) && time.isBefore(close);
    }
}
