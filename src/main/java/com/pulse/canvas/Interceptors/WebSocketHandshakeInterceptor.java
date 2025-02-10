package com.pulse.canvas.Interceptors;

import com.pulse.canvas.Helper.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    JwtHelper jwtHelper;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // Extract the user token from the SecurityContext
         String token = Objects.requireNonNull(request.getHeaders().get("Authorization")).get(0).substring(7);
        System.out.println(token);
        attributes.put("username",jwtHelper.extractUsername(token));


        // Extract query parameters from the WebSocket handshake request URL
        URI uri = request.getURI();
        String canvasId = uri.getQuery(); // Get the full query string

        // If you want to parse individual query parameters (e.g., canvasId)
        if (canvasId != null) {
            String[] params = canvasId.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("canvasId")) {
                    canvasId = keyValue[1]; // Extract canvasId value
                    break;
                }
            }
        }

        attributes.put("canvasId", canvasId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        // Post-handshake actions can be added here if needed
    }
}
