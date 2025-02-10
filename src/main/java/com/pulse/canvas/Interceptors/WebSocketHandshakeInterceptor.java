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

                                try {
                                    // Extract the user token from the SecurityContext
                                    String query = request.getURI().getQuery();
                                    String token = null;
                                    String[] params = query.split("&");
                                    for (String param : params) {
                                        String[] keyValue = param.split("=");
                                        if (keyValue.length == 2 && keyValue[0].equals("token")) {
                                            token = keyValue[1];// Extract token value as String
                                            break;
                                        }
                                    }

                                    if (token != null) {
                                        System.out.println(token);
                                        attributes.put("username", jwtHelper.extractUsername(token));
                                    } else {
                                        throw new IllegalArgumentException("Token parameter not found in query string");
                                    }

                                    // Extract query parameters from the WebSocket handshake request URL
                                    URI uri = request.getURI();


                                    // Parse individual query parameters (e.g., canvasId)
                                    String canvasIdStr = null;
                                    for (String param : params) {
                                        String[] keyValue = param.split("=");
                                        if (keyValue.length == 2 && keyValue[0].equals("canvasId")) {
                                            canvasIdStr = keyValue[1]; // Extract canvasId value as String
                                            break;
                                        }
                                    }

                                    if (canvasIdStr != null) {
                                        try {
                                            Long canvasId = Long.parseLong(canvasIdStr); // Convert canvasId to Long
                                            attributes.put("canvasId", canvasId);
                                            System.out.println("Canvas From Session " + canvasId);
                                        } catch (NumberFormatException e) {
                                            System.err.println("Invalid canvasId format: " + canvasIdStr);
                                            return false;
                                        }
                                    } else {
                                        System.err.println("canvasId parameter not found in query string");
                                        return false;
                                    }

                                    return true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            }

                            @Override
                            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                                       Exception ex) {
                                // Post-handshake actions can be added here if needed
                            }
                        }