package com.zozulia.nanovwap.dto;

import java.util.List;

public record SubscribeMessage(String action, List<String> trades) {
}
