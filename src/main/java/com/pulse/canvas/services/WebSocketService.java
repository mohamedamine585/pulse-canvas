package com.pulse.canvas.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Helper.ByteTransformer;
import com.pulse.canvas.Helper.IntegerTransformers;
import com.pulse.canvas.Helper.RGBAUtils;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class WebSocketService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private CanvasRepository canvasRepository;

    @Autowired
    private CanvasPrintRepository canvasPrintRepository;

    private static final Map<Long, Set<WebSocketSession>> canvasSessions = new ConcurrentHashMap<>();
    private static final Map<Long, Canvas> canvasMap = new ConcurrentHashMap<>();

    public void addClient(WebSocketSession session, Map<Long, CanvasPrintDTO> canvasPrints) throws Exception {
        Long canvasPrintId = null;
        ConcurrentHashMap<Long, Long> print = null;

        Map<String, Object> map = session.getAttributes();
        Long canvasId = (Long) map.get("canvasId");
        String username = (String) map.get("username");

        Artist artist = artistRepository.findByUsername(username);
        if (artist == null) {
            artist = new Artist();
            artist.setUsername("artist" + new Random().nextInt(1000));
            artistRepository.save(artist);
        }

        Canvas canvas = canvasMap.get(canvasId);
        if (canvas == null) {
            canvas = canvasRepository.findById(canvasId).orElse(null);
            if (canvas == null) {
                Canvas newCanvas = new Canvas();
                newCanvas.setName("Canvas " + new Random().nextInt(1000));
                newCanvas.setCreator(artist);
                canvas = canvasRepository.save(newCanvas);
                canvasMap.put(canvasId, canvas);
                CanvasPrint canvasPrint = new CanvasPrint();
                canvasPrint.setCanvas(canvas);
                byte[] byteArray = new byte[0];
                canvasPrint.setPrint(byteArray);
                canvasPrint = canvasPrintRepository.save(canvasPrint);
                canvasPrintId = canvasPrint.getId();
            } else {
                byte[] printVals = canvas.getCanvasPrint().getPrint();
                print = new ConcurrentHashMap<>();
                for (int i = 0; i < printVals.length - 3; i = i + 4) {
                    int r = ByteTransformer.byteToPixel(printVals[i]);
                    int g = ByteTransformer.byteToPixel(printVals[i + 1]);
                    int b = ByteTransformer.byteToPixel(printVals[i + 2]);
                    int a = ByteTransformer.byteToPixel(printVals[i + 3]);
                    int pixel = RGBAUtils.encodeRGBA(r, g, b, a);
                    print.putIfAbsent((long) i / 4, IntegerTransformers.transformIntToLong(pixel));
                }
                canvas = canvasRepository.saveAndFlush(canvas);
            }
        } else {
            print = null;
        }

        session.setBinaryMessageSizeLimit(1024 * 1024 * 10);
        session.setTextMessageSizeLimit(1024 * 1024 * 10);
        canvasSessions.computeIfAbsent(canvasId, k -> new CopyOnWriteArraySet<>()).add(session);

        CanvasPrintDTO canvasPrintDTO = canvasPrints.putIfAbsent(canvasId, new CanvasPrintDTO(canvasId, canvasPrintId, print));
        if (canvasPrintDTO == null) {
            canvasPrintDTO = canvasPrints.get(canvasId);
        }

        print = canvasPrintDTO.getPrint();
        broadcastCanvasPrint(canvasId, MessageType.NEW_USER , session.getId(), new ArrayList<>(print.keySet()), new ArrayList<>(print.values()));

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

    public void broadcastCanvasPrint(Long canvasId, MessageType message , String sessionId, List<Long> updatedPixelsPos, List<Long> updatedPixelsEdits) throws Exception {
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

    public enum MessageType {
        HELLO,
        CANVAS_UPDATE,
        NEW_USER
    }
}