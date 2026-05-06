package com.zozulia.nanovwap.websocket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class AlpacaWebSocketConfig {

    @Bean
    public CommandLineRunner commandLineRunner(AlpacaWebSocketHandler handler) {
        return args ->{
            var client = new StandardWebSocketClient();
            String url = "wss://stream.data.alpaca.markets/v2/iex";
            client.execute(handler, url);
        };
    }
}
