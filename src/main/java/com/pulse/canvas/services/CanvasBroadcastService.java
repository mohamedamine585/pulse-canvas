package com.pulse.canvas.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.shaded.com.google.protobuf.MapEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

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


   // @Scheduled(fixedRate = 2000)

    public  void update(){

        canvasPrints.forEach((canvasId,canvasPrintDTO) ->{
            ConcurrentHashMap<Long,Long> print = canvasPrintDTO.getPrint();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMessage = new HashMap<>();
            jsonMessage.put("message", "New CanvasPrint");
            // Convert byte[] to List<Byte>
            // Convert to unsigned int (0-255)
            List<Long> pos = new ArrayList<>(print.values());
            List<Long> edits =  new ArrayList<>(print.values());
            jsonMessage.put("values", edits);
            jsonMessage.put("positions", pos);
            jsonMessage.put("size", edits.size());
            jsonMessage.put("sessionId","");
            try {
                String jsonString = objectMapper.writeValueAsString(jsonMessage);
                System.out.println("Broadcast");
                for (WebSocketSession session : canvasSessions.get(canvasId)){
                    session.sendMessage(new TextMessage(jsonString));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }
    @Transactional
    public void addClient(WebSocketSession session) throws  Exception{
        try {

          // TODO : DEFINE GLOBAL VARIABLES

            Long canvasPrintId = null ;
            ConcurrentHashMap<Long,Long> print = null;

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
                    byte[] byteArray = new byte[0];

                    canvasPrint.setPrint(byteArray);
                    canvasPrint = canvasPrintRepository.save(canvasPrint);
                    canvasPrintId = canvasPrint.getId();
                } else {
                    print = null;
                    canvas = canvasRepository.saveAndFlush(canvas);
                }

            } else {
                print = null;
            }


            // TODO : UPDATE SERVER STATE


            // TODO : add client session
            session.setBinaryMessageSizeLimit(1024*1024 * 10);
            session.setTextMessageSizeLimit(1024*1024 * 10);
            canvasSessions.computeIfAbsent(canvasId, k -> new CopyOnWriteArraySet<>()).add(session);

            // TODO : init canvasPrint if absent

               CanvasPrintDTO canvasPrintDTO =   canvasPrints.putIfAbsent(canvasId,new CanvasPrintDTO(canvasId,canvasPrintId,print));
               if(canvasPrintDTO == null){
                   canvasPrintDTO = canvasPrints.get(canvasId);
               }

            print  = canvasPrintDTO.getPrint();


            broadcastCanvasPrint(canvasId,session.getId(),new ArrayList<>(print.keySet()),new ArrayList<>(print.values()));


            // TODO : Send session ID

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMessage = new HashMap<>();
            jsonMessage.put("sessionId",session.getId());
            jsonMessage.put("message","hello");
            String jsonString = objectMapper.writeValueAsString(jsonMessage);

            session.sendMessage(new TextMessage(jsonString));
        }catch (Exception e) {
            e.printStackTrace();
            session.sendMessage(new TextMessage("Error creating session"));
            session.close();
        }

    }

    public void removeClient(WebSocketSession session) {
        Long canvasId = (Long) session.getAttributes().get("canvasId");
        canvasSessions.get(canvasId).remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    public void broadcastCanvasPrint(Long canvasId,String sessionId,List<Long> updatedPixelsPos , List<Long> updatedPixelsEdits) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMessage = new HashMap<>();
            jsonMessage.put("message", "New CanvasPrint");
            // Convert byte[] to List<Byte>
            // Convert to unsigned int (0-255)

            jsonMessage.put("values", updatedPixelsEdits);
            jsonMessage.put("positions",updatedPixelsPos);
            jsonMessage.put("size",updatedPixelsPos.size());
            jsonMessage.put("sessionId",sessionId);
            String jsonString = objectMapper.writeValueAsString(jsonMessage);
            System.out.println("Broadcast");
            for (WebSocketSession session : canvasSessions.get(canvasId)) {

                // TODO : big memory overhead
                synchronized (session){
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(jsonString));
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Transactional
    @Async
    public void updateDataBase(Long canvasId ,int biggestPost){
        CanvasPrint canvasPrint = this.canvasPrintRepository.findByCanvasId(canvasId);

        ConcurrentHashMap<Long,Long> pixels = canvasPrints.get(canvasId).getPrint();
        List<Long> updatedPixelPositions = new ArrayList<>(pixels.keySet());
        List<Long> updatedPixelEdits = new ArrayList<>(pixels.values());

        byte[] byteArray = new byte[(int) (biggestPost + 1)];
        for (int i = 0; i < updatedPixelPositions.size(); i++) {
            if(updatedPixelPositions.get(i) < byteArray.length){
                byteArray[Math.toIntExact(updatedPixelPositions.get(i))] = updatedPixelEdits.get(i).byteValue();

            }
        }
        canvasPrint.setPrint(byteArray);
        ;

        canvasPrint = this.canvasPrintRepository.save(canvasPrint);
    }
    @Transactional
    @Async
    public void processUpdate(DrawEvent drawEvent,Long canvasId) {

        try {

            long biggestPost = 0;
            List<Long> updatedPixelsPostions = new ArrayList<>(0);
            List<Long> updatedPixelsEdits = new ArrayList<>(0);



            // TODO : UPDATE SERVER STATE

                    CanvasPrintDTO canvasPrintDTO = canvasPrints.get(canvasId);
                    ConcurrentHashMap<Long,Long> print = canvasPrintDTO.getPrint();
                    Long[] pixelsEdits = drawEvent.getPixelsEdits();
                    Long[] pixelsPositions = drawEvent.getPixelsPositions();


                    if(pixelsPositions.length != pixelsEdits.length){
                        throw new Exception("Invalid DrawEvent: pixelsPositions and pixelsEdits must have the same length");
                    }
                    if(pixelsPositions.length == 0){
                        throw new Exception("Invalid DrawEvent: pixelsPositions and pixelsEdits must have the same length");
                    }



                    // TODO : O(n)
                    for (int i = 0; i < pixelsPositions.length; i++) {
                        if(pixelsPositions[i] < 0){
                            throw new Exception("Invalid DrawEvent: pixelsPositions must be within the bounds of the canvas");
                        }
                        // TODO : to check the byte array
                      /*  if(print[i] != 255){
                            throw new Exception("Invalid DrawEvent: pixel at position " + pixelsPositions[i] + " is already colored");
                        }*/

                            print.put(pixelsPositions[i] , pixelsEdits[i]);
                            updatedPixelsPostions.add(pixelsPositions[i]);
                            updatedPixelsEdits.add(pixelsEdits[i]);
                            if(pixelsPositions[i] > biggestPost){
                                biggestPost = pixelsPositions[i];
                            }
                        }

                    canvasPrintDTO.setPrint(print);
                    canvasPrints.put(canvasPrintDTO.getCanvasId(),canvasPrintDTO);

            // TODO : UPDATE BD STATE



            updateDataBase(canvasId, (int) biggestPost);


            // TODO : BROADCAST TO CLIENTS
                    broadcastCanvasPrint(canvasId, drawEvent.getSessionId(), updatedPixelsPostions,updatedPixelsEdits);

        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
