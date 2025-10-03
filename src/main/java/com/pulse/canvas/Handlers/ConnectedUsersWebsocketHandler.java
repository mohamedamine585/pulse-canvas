package com.pulse.canvas.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.ConnectedUserDTO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class ConnectedUsersWebsocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, List<WebSocketSession>> clients = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<Long,List<ConnectedUserDTO>> connectedUsers = new java.util.concurrent.ConcurrentHashMap<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        assert uri != null;
        if (uri.getQuery() != null && uri.getQuery().contains("canvasId")) {
            String query = uri.getQuery(); // e.g., canvasId=1
            String canvasIdStr = query.split("=")[1];
            session.getAttributes().put("canvasId", Long.parseLong(canvasIdStr));
        }
        Long canvasId = (Long) session.getAttributes().get("canvasId");
        clients.computeIfAbsent(canvasId, k -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(session);


        System.out.println("New connection established for canvasId: " + canvasId);
      for (ConnectedUserDTO user : connectedUsers.getOrDefault(canvasId, List.of())) {
                    sendUserConnectedToClients(session, user);

            }

    }

    @EventListener(classes = ConnectedUserDTO.class)
    private void handleUserConnectedEvent(ConnectedUserDTO connectedUserDTO) {

        // Send event to all sessions for the canvas
          clients.getOrDefault(connectedUserDTO.getCanvasId(), List.of())
                .forEach(session -> sendUserConnectedToClients(session, connectedUserDTO));
        List<ConnectedUserDTO> usersList = connectedUsers.computeIfAbsent(connectedUserDTO.getCanvasId(), k -> new java.util.concurrent.CopyOnWriteArrayList<>());
        if (usersList.stream().noneMatch(user -> user.getUserId().equals(connectedUserDTO.getUserId()))) {
            usersList.add(connectedUserDTO);
        }

    }

    private void sendUserConnectedToClients(WebSocketSession session, ConnectedUserDTO connectedUserDTO) {
        try {
            if(session.isOpen()){
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(connectedUserDTO)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
