package com.pulse.canvas.configurations;

import com.pulse.canvas.Handlers.CanvasWebSocketHandler;
import com.pulse.canvas.Interceptors.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

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
        registry.addHandler(canvasWebSocketHandler, "/live/canvas")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*"); // Adjust for production security
    }
}
