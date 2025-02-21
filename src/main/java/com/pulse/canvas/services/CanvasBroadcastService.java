package com.pulse.canvas.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Helper.IntegerTransformers;
import com.pulse.canvas.Helper.RGBAUtils;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.shaded.com.google.protobuf.MapEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    private static final ConcurrentLinkedQueue<Runnable> dbUpdates = new ConcurrentLinkedQueue<>();

    @Autowired
    private ThreadPoolTaskExecutor canvasThreadPool;

    @Autowired
    private ThreadPoolTaskExecutor dbThreadPool;


   @Scheduled(fixedRate = 5000)
    public  void update(){
       try {
           Runnable task;
           while ((task = dbUpdates.poll()) != null) {
               task.run();
           }


       }catch (Exception e){
           e.printStackTrace();
       }

   }
    private void LoadCanvasFromDb(Long canvasId){
        try {
            Canvas canvas = this.canvasRepository.getReferenceById(canvasId);
            CanvasPrint canvasPrint = canvas.getCanvasPrint();
            byte[] print = canvasPrint.getPrint();
            List<Long> positions = new ArrayList<>();
            List<Long> values = new ArrayList<>();
            for(int i = 0 ; i < print.length - 4 ; i += 4){
                positions.add((long) i);
                values.add((long) print[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public void updateDataBase(Long canvasId, int biggestPos) {
        System.out.println("Canvas Id " + canvasId);

        CanvasPrint canvasPrint = this.canvasPrintRepository.findByCanvasId(canvasId);
        if (canvasPrint == null) {
            throw new EntityNotFoundException("CanvasPrint not found with canvasId: " + canvasId);
        }

        ConcurrentHashMap<Long, Long> pixels = canvasPrints.get(canvasId).getPrint();
        List<Long> updatedPixelPositions = new ArrayList<>(pixels.keySet());
        List<Long> updatedPixelEdits = new ArrayList<>(pixels.values());

        byte[] byteArray = new byte[biggestPos * 4 + 1];

        for (int i = 0; i < updatedPixelPositions.size(); i++) {

            int pixelValue = Math.toIntExact(IntegerTransformers.transformLongToInt(updatedPixelEdits.get(i)));
            int[] rgba = RGBAUtils.decodeRGBA(IntegerTransformers.transformIntToLong(pixelValue));

            // Vérification des indices pour éviter les IndexOutOfBounds
            int position = Math.toIntExact(updatedPixelPositions.get(i));
            if (position + 3 < byteArray.length) {
                byteArray[position] = (byte) rgba[0];
                byteArray[position + 1] = (byte) rgba[1];
                byteArray[position + 2] = (byte) rgba[2];
                byteArray[position + 3] = (byte) rgba[3];
            }
        }

        canvasPrint.setPrint(byteArray);
        this.canvasPrintRepository.save(canvasPrint);
    }

    @Async
    @Transactional
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


            final int biggestPostFinal = (int) biggestPost;

            dbUpdates.add(()-> updateDataBase(canvasId,biggestPostFinal));


            // TODO : BROADCAST TO CLIENTS
                    broadcastCanvasPrint(canvasId, drawEvent.getSessionId(), updatedPixelsPostions,updatedPixelsEdits);

        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
