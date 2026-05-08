package com.zozulia.nanovwap.websocket;

import com.zozulia.nanovwap.dto.AuthMessage;
import com.zozulia.nanovwap.dto.SubscribeMessage;
import com.zozulia.nanovwap.dto.TradeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

@Component
public class AlpacaWebSocketHandler extends TextWebSocketHandler {

    @Value("${alpaca.api.key}")
    private String apiKey;
    @Value("${alpaca.api.secret}")
    private String apiSecret;

    private final ObjectMapper objectMapper;

    private final String targetSymbol = "AAPL";
    private double sumPriceVolume = 0.0;
    private double totalVolume = 0.0;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode rootNode = objectMapper.readTree(payload);
        JsonNode event = rootNode.get(0);
        String messageType = event.get("T").asText();

        if(messageType.equals("success") && event.get("msg").asText().equals("connected")) {
            AuthMessage authMessage = new AuthMessage("auth", apiKey, apiSecret);
            String jsonString = objectMapper.writeValueAsString(authMessage);
            session.sendMessage(new TextMessage(jsonString));

            System.out.println("An authorization request has been sent");
        }
        else if(messageType.equals("success") && event.get("msg").asText().equals("authenticated")) {
            List<String> subscribeList = new LinkedList<>();
            subscribeList.add("AAPL");

            SubscribeMessage subscribeMessage = new SubscribeMessage("subscribe", subscribeList);
            String jsonSubscribeListString = objectMapper.writeValueAsString(subscribeMessage);
            session.sendMessage(new TextMessage(jsonSubscribeListString));

            System.out.println("A subscription has been sent");
        }

        else if(messageType.equals("t")) {
            TradeMessage trade = objectMapper.treeToValue(event, TradeMessage.class);
            System.out.println("Deal info: " + trade.S() + " by " + trade.p() + ", deal size: " + trade.s());

            if (trade.S().equals(targetSymbol)) {
                sumPriceVolume += trade.p() * trade.s();
                totalVolume += trade.s();
                double currentVwap = sumPriceVolume / totalVolume;

                System.out.println("--- NEW TRADE ---");
                System.out.println("Price: " + trade.p() + " | Size: " + trade.s());
                System.out.println("Current VWAP: " + currentVwap);
            }
        }
    }

    public AlpacaWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
