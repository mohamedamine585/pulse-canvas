package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class CanvasBroadcastService {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    CanvasRepository canvasRepository;

    @Autowired
    CanvasPrintRepository canvasPrintRepository;
    // A thread-safe Set to hold connected WebSocket clients
    private static final Map<Long, Set<WebSocketSession>> canvasSessions = new HashMap<>();
    private static final Set<WebSocketSession> clients = new CopyOnWriteArraySet<>();
    private static final Map<Long, CanvasPrintDTO> canvasPrints = new HashMap<>();
    private static final Map<Long,Canvas> canvasMap = new HashMap<>();


    @Transactional
    public void addClient(WebSocketSession session) throws  Exception{
        try {
            CanvasPrintDTO canvasPrintDTO = new CanvasPrintDTO();
            Map<String, Object> map = new HashMap<>();
            map = session.getAttributes();
            Long canvasId = Long.parseLong(map.get("canvasId").toString().split("=")[1]);
            String username = (String) map.get("username");


            Artist artist = artistRepository.findByUsername(username);
            if (artist == null) {
                artist = new Artist();
                artist.setUsername("artist" + new Random().nextInt(1000));
                artistRepository.save(artist);
            }

            System.out.println("Canvas Id " + canvasId);

            Canvas canvas = canvasMap.get(canvasId);
            if (canvas == null) {
                canvas = canvasRepository.findById(canvasId).orElse(null);
                if (canvas == null) {
                    // Handle canvas creation if needed
                    Canvas newCanvas = new Canvas();
                    newCanvas.setName("Canvas " + new Random().nextInt(1000));
                    canvas = canvasRepository.save(newCanvas);
                    CanvasPrint canvasPrint = new CanvasPrint();
                    canvasPrint.setCanvas(canvas);
                    canvasPrint.setPrint(new byte[10000]);
                    canvasPrint = canvasPrintRepository.save(canvasPrint);
                    canvasPrintDTO.setCanvasId(canvasPrint.getId());
                    canvasPrintDTO.setPrint(canvasPrint.getPrint());
                } else {
                    canvas = canvasRepository.saveAndFlush(canvas);
                }

            }



            // Add the session to the clients set and update the maps
            clients.add(session);
            canvasPrints.putIfAbsent(canvas.getId(), canvasPrintDTO);
            canvasPrintDTO = canvasPrints.get(canvasId);

            System.out.println(clients.size());
            System.out.println(canvasPrints.size());

            System.out.println("canvas " + canvas.getId());


            broadcastCanvasPrint(canvasPrintDTO);
        }catch (Exception e) {
            session.sendMessage(new TextMessage("Error creating session"));
            session.close();
        }

    }

    public void removeClient(WebSocketSession session) {
        clients.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    public void broadcastCanvasPrint(CanvasPrintDTO canvasPrint) throws IOException {
        for (WebSocketSession session : clients) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("New CanvasPrint: " + Arrays.toString(canvasPrint.getPrint())));
            }
        }
    }
    public void broadcastToClients(DrawEvent drawEvent) throws IOException {
        // Here we convert the event to a TextMessage (or BinaryMessage depending on your need)
        for (WebSocketSession session : clients) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("New Draw Event: " + drawEvent));
            }
        }
    }
}
