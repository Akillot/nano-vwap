package com.zozulia.nanovwap.websocket;

import com.zozulia.nanovwap.dto.AuthMessage;
import com.zozulia.nanovwap.dto.SubscribeMessage;
import com.zozulia.nanovwap.dto.TradeMessage;
import com.zozulia.nanovwap.service.VwapCalculator;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

public class AlpacaWebSocketHandler extends TextWebSocketHandler {

    private final String currentTicker;
    private final String apiKey;
    private final String apiSecret;

    private final ObjectMapper objectMapper;
    private final VwapCalculator vwapCalculator = new VwapCalculator();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode rootNode = objectMapper.readTree(payload);

        if (rootNode.isArray()) {
            for (JsonNode event : rootNode) {
                String messageType = event.get("T").asText();

                if (messageType.equals("success") && event.get("msg").asText().equals("connected")) {
                    AuthMessage authMessage = new AuthMessage("auth", apiKey, apiSecret);
                    String jsonString = objectMapper.writeValueAsString(authMessage);
                    session.sendMessage(new TextMessage(jsonString));
                    System.out.println("--- An authorization request has been sent");
                }
                else if (messageType.equals("success") && event.get("msg").asText().equals("authenticated")) {
                    List<String> subscribeList = new LinkedList<>();
                    subscribeList.add(currentTicker);

                    SubscribeMessage subscribeMessage = new SubscribeMessage("subscribe", subscribeList);
                    String jsonSubscribeListString = objectMapper.writeValueAsString(subscribeMessage);
                    session.sendMessage(new TextMessage(jsonSubscribeListString));
                    System.out.println("--- A subscribe request has been sent");
                }
                else if (messageType.equals("subscription")){
                    System.out.println("--- Subscribed to trades: " + event.get("trades"));
                }
                else if (messageType.equals("error")) {
                    System.out.println("--- Alpaca error " + event.get("code").asInt() + ": " + event.get("msg").asText());
                    session.close();
                }
                else if (messageType.equals("t")) {
                    TradeMessage trade = objectMapper.treeToValue(event, TradeMessage.class);

                    if (trade.S().equals(currentTicker)) {
                        System.out.println("Deal info: " + trade.S() + " | Price: " + trade.p() + " | Size: " + trade.s());
                        System.out.println("Current VWAP: " + vwapCalculator.add(trade.p(), trade.s()));
                        System.out.println("--------------------------------");
                    }
                }
            }
        }
    }

    public AlpacaWebSocketHandler(ObjectMapper objectMapper, String currentTicker, String apiKey, String apiSecret) {
        this.objectMapper = objectMapper;
        this.currentTicker = currentTicker;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
}
