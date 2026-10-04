package com.zozulia.nanovwap.websocket;

import com.zozulia.nanovwap.dto.AuthMessage;
import com.zozulia.nanovwap.dto.SubscribeMessage;
import com.zozulia.nanovwap.dto.TradeMessage;
import com.zozulia.nanovwap.service.VwapCalculator;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class AlpacaWebSocketHandler extends TextWebSocketHandler {

    private final String currentTicker;
    private final String apiKey;
    private final String apiSecret;
    private final String url;

    private static final int RECONNECT_DELAY_TIME_SECONDS = 10;
    private volatile boolean reconnectEnabled = true;

    private final ObjectMapper objectMapper;
    private final VwapCalculator vwapCalculator = new VwapCalculator();
    private final StandardWebSocketClient client = new StandardWebSocketClient();

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
                    SubscribeMessage subscribeMessage = new SubscribeMessage("subscribe", List.of(currentTicker));
                    String jsonSubscribeListString = objectMapper.writeValueAsString(subscribeMessage);

                    session.sendMessage(new TextMessage(jsonSubscribeListString));
                    System.out.println("--- A subscribe request has been sent");
                }
                else if (messageType.equals("subscription")){
                    System.out.println("--- Subscribed to trades: " + event.get("trades"));
                }
                else if (messageType.equals("error")) {
                    System.out.println("--- Alpaca error " + event.get("code").asInt() + ": " + event.get("msg").asText());
                    reconnectEnabled = false;
                    session.close();
                }
                else if (messageType.equals("t")) {
                    TradeMessage trade = objectMapper.treeToValue(event, TradeMessage.class);

                    if (trade.S().equals(currentTicker)) {
                        LocalDateTime localTime = trade.t()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                                .truncatedTo(ChronoUnit.SECONDS);

                        System.out.println("Deal info: " + trade.S() + " | Price: " + trade.p() + " | Size: " + trade.s() + " | Timestamp: " + localTime);
                        System.out.println("Current VWAP: " + vwapCalculator.add(trade.p(), trade.s()));
                        System.out.println("--------------------------------");
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.err.println("--- Session has been closed: " + session.getId() + " with status: " + status);
        scheduleReconnect();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("--- Transport error in session: " + session.getId() + ". Exception: " + exception);
    }

    public void connect(){
        client.execute(this, url).whenComplete((result, exception) -> {
            if(exception != null){
                System.err.println("--- Connection failed: " + exception.getMessage());
                scheduleReconnect();
            }
        });
    }

    private void scheduleReconnect(){
        if(!reconnectEnabled){
            System.err.println("--- --- Reconnect disabled due to fatal error");
            return;
        }

        Executor delayed = CompletableFuture.delayedExecutor(RECONNECT_DELAY_TIME_SECONDS, TimeUnit.SECONDS);
        System.err.println("--- --- Reconnecting in " + RECONNECT_DELAY_TIME_SECONDS + " seconds");
        CompletableFuture.runAsync(this::connect,  delayed);
    }

    public AlpacaWebSocketHandler(ObjectMapper objectMapper, String currentTicker, String apiKey, String apiSecret, String url) {
        this.objectMapper = objectMapper;
        this.currentTicker = currentTicker;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.url = url;
    }
}
