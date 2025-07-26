package com.chatbot.thinker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.chatbot.thinker.websocket.WebSocketServer;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketServer server; 
    private final AppConfigProperties appConfig;

    public WebSocketConfig(AppConfigProperties appConfig, WebSocketServer server) {
        this.appConfig = appConfig;
        this.server = server;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(server, "/chatbot")
                .setAllowedOriginPatterns("*");
    }
}
