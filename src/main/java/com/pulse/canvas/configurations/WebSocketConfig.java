package com.pulse.canvas.configurations;

import com.pulse.canvas.Handlers.CanvasWebSocketHandler;
import com.pulse.canvas.Handlers.ConnectedUsersWebsocketHandler;
import com.pulse.canvas.Handlers.LiveEventsWebSocketHandler;
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
    private final CanvasWebSocketHandler canvasWebSocketHandler;

    @Autowired
    private final WebSocketHandshakeInterceptor handshakeInterceptor;

    @Autowired
    private final LiveEventsWebSocketHandler liveEventsWebSocketHandler;

    @Autowired
    private final ConnectedUsersWebsocketHandler connectedUsersWebsocketHandler;
    public WebSocketConfig(CanvasWebSocketHandler canvasWebSocketHandler, WebSocketHandshakeInterceptor handshakeInterceptor, LiveEventsWebSocketHandler liveEventsWebSocketHandler,ConnectedUsersWebsocketHandler connectedUsersWebsocketHandler) {
        this.canvasWebSocketHandler = canvasWebSocketHandler;
        this.handshakeInterceptor = handshakeInterceptor;
        this.liveEventsWebSocketHandler = liveEventsWebSocketHandler;
        this.connectedUsersWebsocketHandler = connectedUsersWebsocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(canvasWebSocketHandler, "/live/canvas")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOriginPatterns("*");

        registry.addHandler(connectedUsersWebsocketHandler, "/live/connected-users")
                .setAllowedOriginPatterns("*");

        registry.addHandler(liveEventsWebSocketHandler, "/live/events")
                .setAllowedOriginPatterns("*");
    }

}
