package com.pulse.canvas.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.UserLiveEventDTO;
import com.pulse.canvas.services.LiveEventService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class LiveEventsWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, List<WebSocketSession>> clients = new java.util.concurrent.ConcurrentHashMap<>();
    public LiveEventsWebSocketHandler(LiveEventService liveEventsService) {
    }
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

    }

    @EventListener(classes = UserLiveEventDTO.class)
    private void handleUserLiveEvent(UserLiveEventDTO event) {
        // Log event type
        switch (event.getUserEventType()) {
            case USER_JOINED -> System.out.println("Handling JOIN event for user: " + event.getUserId());
            case  USER_LEFT -> System.out.println("Handling LEAVE event for user: " + event.getUserId());
            default -> System.out.println("Unknown event type: " + event.getEventType());
        }

        // Send event to all sessions for the canvas
        clients.getOrDefault(event.getCanvasId(), List.of())
                .forEach(session -> sendEventToSession(session, event));
    }

    private void sendEventToSession(WebSocketSession session, UserLiveEventDTO event) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Long canvasId = (Long) session.getAttributes().get("canvasId");
        List<WebSocketSession> sessions = clients.get(canvasId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                clients.remove(canvasId);
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
