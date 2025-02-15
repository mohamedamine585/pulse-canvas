package com.pulse.canvas.configurations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
public class WebSocketTransportConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(1024 * 1024 * 10); // Set message size limit (512 KB)
        registry.setSendBufferSizeLimit(1024 * 1024 * 10); // Set send buffer size (1 MB)
    }
}
