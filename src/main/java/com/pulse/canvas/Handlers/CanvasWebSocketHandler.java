package com.pulse.canvas.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.services.CanvasBroadcastService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CanvasWebSocketHandler extends BinaryWebSocketHandler {

    private final CanvasBroadcastService broadcastService;

    public CanvasWebSocketHandler(CanvasBroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        // Handle incoming BinaryMessage (for example, drawing data or CanvasPrint)
        byte[] payload = message.getPayload().array();
        // Process the binary data (e.g., canvas drawing events)

        // For simplicity, let's assume that the incoming message is a DrawEvent or CanvasPrint
        ObjectMapper objectMapper = new ObjectMapper();
        DrawEvent drawEvent = objectMapper.readValue(payload, DrawEvent.class);

        // Broadcast the event to all connected clients
        broadcastService.broadcastToClients(drawEvent);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // When a new WebSocket connection is established, add the session to the list of clients


        broadcastService.addClient(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // When a connection is closed, remove the session from the list of clients
        broadcastService.removeClient(session);
    }
}
