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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private static final Map<Long, Set<WebSocketSession>> canvasSessions = new ConcurrentHashMap<>();
    private static final Map<Long, CanvasPrintDTO> canvasPrints = new ConcurrentHashMap<>();
    private static final Map<Long,Canvas> canvasMap = new ConcurrentHashMap<>();


    @Transactional
    public void addClient(WebSocketSession session) throws  Exception{
        try {

          // TODO : DEFINE GLOBAL VARIABLES

            Long canvasPrintId = null ;
            byte[] print = null;

            // TODO : SETUP PARAMETERS FROM SESSION
            Map<String, Object> map = new HashMap<>();
            map = session.getAttributes();
            Long canvasId = (Long) map.get("canvasId");
            String username = (String) map.get("username");


            // TODO : UPDATE DATABASE STATE

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
                    // Handle canvas creation if needed
                    Canvas newCanvas = new Canvas();
                    newCanvas.setName("Canvas " + new Random().nextInt(1000));
                    newCanvas.setCreator(artist);
                    canvas = canvasRepository.save(newCanvas);
                    canvasMap.put(canvasId,canvas);
                    CanvasPrint canvasPrint = new CanvasPrint();
                    canvasPrint.setCanvas(canvas);
                    print = new byte[10];
                    canvasPrint.setPrint(print);
                    canvasPrint = canvasPrintRepository.save(canvasPrint);
                    canvasPrintId = canvasPrint.getId();
                } else {
                    canvas = canvasRepository.saveAndFlush(canvas);
                }

            }


            // TODO : UPDATE SERVER STATE


            // TODO : add client session
            canvasSessions.computeIfAbsent(canvasId, k -> new CopyOnWriteArraySet<>()).add(session);

            // TODO : init canvasPrint if absent

            if(canvasPrintId != null || print != null){
                canvasPrints.putIfAbsent(canvasId,new CanvasPrintDTO(canvasId,canvasPrintId,print));
            }



            System.out.println(canvasSessions.size());
            System.out.println(canvasPrints.size());

            System.out.println("canvas " + canvas.getId());


            broadcastCanvasPrint(canvasId);
        }catch (Exception e) {
            session.sendMessage(new TextMessage("Error creating session"));
            session.close();
        }

    }

    public void removeClient(WebSocketSession session) {
        Long canvasId = (Long) session.getAttributes().get("canvasId");
        canvasSessions.get(canvasId).remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    public void broadcastCanvasPrint(Long canvasId) throws IOException {
        CanvasPrintDTO canvasPrint = canvasPrints.get(canvasId);
        for (WebSocketSession session : canvasSessions.get(canvasPrint.getCanvasId())) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("New CanvasPrint: " + Arrays.toString(canvasPrint.getPrint())));
            }
        }
    }
    @Async
    public void processUpdate(DrawEvent drawEvent) {

        try {
            for (WebSocketSession session : canvasSessions.get(drawEvent.getCanvasId())) {
                if (session.isOpen()) {

                    // TODO : UPDATE SERVER STATE

                    CanvasPrintDTO canvasPrintDTO = canvasPrints.get(drawEvent.getCanvasId());
                    byte[] print = canvasPrintDTO.getPrint();
                    byte[] pixelsEdits = drawEvent.getPixelsEdits();
                    int[] pixelsPositions = drawEvent.getPixelsPositions();

                    if(pixelsPositions.length != pixelsEdits.length){
                        throw new Exception("Invalid DrawEvent: pixelsPositions and pixelsEdits must have the same length");
                    }
                    if(pixelsPositions.length > print.length){
                        throw new Exception("Invalid DrawEvent: pixelsPositions and pixelsEdits must be within the bounds of the canvas");
                    }
                    if(pixelsPositions.length == 0){
                        throw new Exception("Invalid DrawEvent: pixelsPositions and pixelsEdits must have the same length");
                    }

                    // TODO : O(n)
                    for (int i = 0; i < pixelsPositions.length; i++) {
                        if(pixelsPositions[i] >= print.length || pixelsPositions[i] < 0){
                            throw new Exception("Invalid DrawEvent: pixelsPositions must be within the bounds of the canvas");
                        }

                        // TODO : to check the byte array
                      /*  if(print[i] != 255){
                            throw new Exception("Invalid DrawEvent: pixel at position " + pixelsPositions[i] + " is already colored");
                        }*/
                        if(print[i] != pixelsEdits[i]) {
                            print[pixelsPositions[i]] = pixelsEdits[i];
                        }
                    }
                    canvasPrintDTO.setPrint(print);
                    canvasPrints.put(canvasPrintDTO.getCanvasId(),canvasPrintDTO);

                    // TODO : BROADCAST TO CLIENTS
                    broadcastCanvasPrint(canvasPrintDTO.getCanvasId());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
