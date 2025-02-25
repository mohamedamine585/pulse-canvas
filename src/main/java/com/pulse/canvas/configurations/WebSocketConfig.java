package com.pulse.canvas.configurations;

import com.pulse.canvas.Handlers.CanvasWebSocketHandler;
import com.pulse.canvas.Interceptors.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    @Autowired
    private final WebSocketHandler canvasWebSocketHandler;

    @Autowired
    private final HandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(CanvasWebSocketHandler canvasWebSocketHandler, WebSocketHandshakeInterceptor handshakeInterceptor) {
        this.canvasWebSocketHandler = canvasWebSocketHandler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(canvasWebSocketHandler, "/canvas")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*"); // Adjust for production security
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}
