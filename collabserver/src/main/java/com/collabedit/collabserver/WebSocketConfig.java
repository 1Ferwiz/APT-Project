package com.collabedit.collabserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket//override registerWebSocketHandlers()
// and tell Spring which class should handle WebSocket connections.

// Spring config class that also enables WebSocket support and implements WebSocketConfigurer.
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired//injects your WebSocket handler class —
    // Spring will automatically create the instance because that class is marked with @Component.
    private CollabWebSocketHandler collabWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(collabWebSocketHandler, "/ws/edit")
                .setAllowedOrigins("*");
    }//registers my CollabWebSocketHandler to the /ws/edit WebSocket endpoint.
}
