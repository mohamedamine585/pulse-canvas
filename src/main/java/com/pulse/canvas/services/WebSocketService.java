package com.pulse.canvas.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Helper.PixelMapBuilder;
import com.pulse.canvas.Helper.jwt.JwtTokenFilter;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import com.pulse.canvas.enums.MessageType;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class WebSocketService {

    @Autowired
    private CanvasService canvasService;




    private static final Map<Long, Set<WebSocketSession>> canvasSessions = new ConcurrentHashMap<>();
    private static final Map<Long, Canvas> canvasMap = new ConcurrentHashMap<>();

    public void addClient(WebSocketSession session, Map<Long, CanvasPrintDTO> canvasPrints) throws Exception {
        Map<String, Object> map = session.getAttributes();
        Long canvasId = (Long) map.get("canvasId");
        Long userId = (Long) map.get("userId");
        if (canvasId == null) {
            throw new Exception("Canvas ID is missing");
        }


        Artist artist = canvasService.getOrCreateArtist(userId);
        if(artist == null){
            return;
        }

        Canvas canvas = canvasService.getOrCreateCanvas(canvasId, artist);
        if(canvas == null) {
            throw new Exception("Cannot find canvas");
        }
        CanvasPrint canvasPrint = canvasService.getOrCreateCanvasPrint(canvas);

        session.setBinaryMessageSizeLimit(1024 * 1024 * 10);
        session.setTextMessageSizeLimit(1024 * 1024 * 10);
        canvasSessions.computeIfAbsent(canvasId, k -> new CopyOnWriteArraySet<>()).add(session);

        byte[] print = canvasPrint.getPrint();

        ConcurrentHashMap<Long, Long> printMap = PixelMapBuilder.buildPixelMap(print);
        CanvasPrintDTO canvasPrintDTO = canvasPrints.putIfAbsent(canvasId, new CanvasPrintDTO(canvasId, canvasPrint.getId(), printMap));
        if (canvasPrintDTO == null) {

            canvasPrintDTO = canvasPrints.get(canvasId);
        }

        broadcastCanvasPrint(canvasId, MessageType.NEW_USER, session.getId(), new ArrayList<>(canvasPrintDTO.getPrint().keySet()), new ArrayList<>(canvasPrintDTO.getPrint().values()));
        sendJsonMessage(session, MessageType.HELLO, "hello" + canvas.getName());
    }

    public void removeClient(WebSocketSession session) {
        Long canvasId = (Long) session.getAttributes().get("canvasId");
        canvasSessions.get(canvasId).remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    public void sendJsonMessage(WebSocketSession session, MessageType messageType, String message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMessage = new HashMap<>();
        jsonMessage.put("sessionId", session.getId());
        jsonMessage.put("messageType", messageType);
        jsonMessage.put("message", message);
        String jsonString = objectMapper.writeValueAsString(jsonMessage);

        session.sendMessage(new TextMessage(jsonString));
    }

    public void broadcastCanvasPrint(Long canvasId, MessageType message, String sessionId, List<Long> updatedPixelsPos, List<Long> updatedPixelsEdits) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMessage = new HashMap<>();
        jsonMessage.put("messageType", message);
        jsonMessage.put("values", updatedPixelsEdits);
        jsonMessage.put("positions", updatedPixelsPos);
        jsonMessage.put("size", updatedPixelsPos.size());
        jsonMessage.put("sessionId", sessionId);
        String jsonString = objectMapper.writeValueAsString(jsonMessage);

        for (WebSocketSession session : canvasSessions.get(canvasId)) {
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonString));
                }
            }
        }
    }


}