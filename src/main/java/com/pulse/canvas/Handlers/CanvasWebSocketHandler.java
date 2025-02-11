package com.pulse.canvas.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.services.CanvasBroadcastService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CanvasWebSocketHandler extends TextWebSocketHandler {

    private final CanvasBroadcastService canvasBroadcastService;

    public CanvasWebSocketHandler(CanvasBroadcastService broadcastService) {
        this.canvasBroadcastService = broadcastService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)  {
        try {
            // Handle incoming TextMessage (for example, drawing data or CanvasPrint)
            String payload = message.getPayload();

            // Process the JSON data (e.g., canvas drawing events)
            ObjectMapper objectMapper = new ObjectMapper();
            DrawEvent drawEvent = objectMapper.readValue(payload, DrawEvent.class);

            System.out.println("Received draw event for canvas " + session.getAttributes().get("canvasId"));


            // TODO : PROCESS DRAW EVENT
            canvasBroadcastService.processUpdate(drawEvent,(Long) session.getAttributes().get("canvasId"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // When a new WebSocket connection is established, add the session to the list of clients


        canvasBroadcastService.addClient(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // When a connection is closed, remove the session from the list of clients
        canvasBroadcastService.removeClient(session);
    }
}
