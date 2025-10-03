package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Helper.RGBAUtils;
import com.pulse.canvas.enums.MessageType;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class CanvasPrintService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private CanvasSyncService canvasSyncService;

    @Autowired
    private PrintEditService printEditService;

    @Async
    public void processUpdate(DrawEvent drawEvent, Map<Long, CanvasPrintDTO> canvasPrints, ConcurrentLinkedQueue<Runnable> dbUpdates) {
        try {
            List<Long> updatedPixelsPostions = new ArrayList<>();
            List<Long> updatedPixelsEdits = new ArrayList<>();

            // TODO : Process DrawEvent
            int biggestPosIndx = processDrawEvent(drawEvent, canvasPrints, updatedPixelsPostions, updatedPixelsEdits);

            // TODO : Sync with other instances
            canvasSyncService.sendCanvasToSync(drawEvent);
            // TODO : Broadcast to all clients
            webSocketService.broadcastCanvasPrint(drawEvent.getCanvasId(), MessageType.CANVAS_UPDATE, drawEvent.getSessionId(), updatedPixelsPostions, updatedPixelsEdits);



            savePixelsUpdate(updatedPixelsPostions,updatedPixelsEdits,biggestPosIndx,Instant.now(),drawEvent.getUserId());



            // TODO : Update Database
            updateDatabase(drawEvent.getCanvasId(), updatedPixelsPostions, updatedPixelsEdits, dbUpdates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void processSyncUpdate(DrawEvent drawEvent, Map<Long, CanvasPrintDTO> canvasPrints) {
        try {
            List<Long> updatedPixelsPostions = new ArrayList<>();
            List<Long> updatedPixelsEdits = new ArrayList<>();

            // TODO : Process DrawEvent
            int biggestPosIndx =  processDrawEvent(drawEvent, canvasPrints, updatedPixelsPostions, updatedPixelsEdits);

            if(biggestPosIndx == -1)
                return;

            // TODO : Broadcast to all clients
            webSocketService.broadcastCanvasPrint(drawEvent.getCanvasId(), MessageType.CANVAS_UPDATE, drawEvent.getSessionId(), updatedPixelsPostions, updatedPixelsEdits);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void savePixelsUpdate(List<Long> updatedPixelsPostions, List<Long> updatedPixelsEdits, int biggestPosIndx, Instant editTime,Long artistId){
        try {
            byte[] printUpdate = new byte[biggestPosIndx * 4 + 1];


           for(int i = 0 ; i < updatedPixelsPostions.size() - 3 ; i += 4) {
                byte[] rgba = RGBAUtils.decodeRGBA(updatedPixelsEdits.get(i));
                printUpdate[Math.toIntExact(updatedPixelsPostions.get(i))] = rgba[0];
                printUpdate[Math.toIntExact(updatedPixelsPostions.get(i + 1))] = rgba[1];
                printUpdate[Math.toIntExact(updatedPixelsPostions.get(i + 2))] = rgba[2];
                printUpdate[Math.toIntExact(updatedPixelsPostions.get(i + 3))] = rgba[3];

           }
           printEditService.saveCanvasPrintEdit(artistId,printUpdate,editTime);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int processDrawEvent(DrawEvent drawEvent, Map<Long, CanvasPrintDTO> canvasPrints, List<Long> updatedPixelsPostions, List<Long> updatedPixelsEdits) throws Exception {
        Long canvasId = drawEvent.getCanvasId();
        CanvasPrintDTO canvasPrintDTO = canvasPrints.get(canvasId);
        if (canvasPrintDTO == null) {
            return -1;
        }
        ConcurrentHashMap<Long, Long> print = canvasPrintDTO.getPrint();
        Long[] pixelsEdits = drawEvent.getPixelsEdits();
        Long[] pixelsPositions = drawEvent.getPixelsPositions();
        int biggestPosIndex = 0;

        if (pixelsPositions.length != pixelsEdits.length) {
            throw new Exception("Invalid DrawEvent: pixelsPositions and pixelsEdits must have the same length");
        }
        if (pixelsPositions.length == 0) {
            throw new Exception("Invalid DrawEvent: pixelsPositions and pixelsEdits must have the same length");
        }

        for (int i = 0; i < pixelsPositions.length; i++) {
            if (pixelsPositions[i] < 0) {
                throw new Exception("Invalid DrawEvent: pixelsPositions must be within the bounds of the canvas");
            }
            if(pixelsPositions[i] > 800*600){
                throw new Exception("Invalid DrawEvent: pixelsPositions must be within the bounds of the canvas");
            }
            // TODO : Check if the event timestamp is older than the latest update or if the pixel is virgin
            if (print.containsKey(pixelsPositions[i])) {
                if (print.get(pixelsPositions[i]).equals(pixelsEdits[i])) {
                    continue;
                }
            }
            if(pixelsPositions[i] > biggestPosIndex){
                biggestPosIndex = Math.toIntExact(pixelsPositions[i]);
            }
            print.put(pixelsPositions[i], pixelsEdits[i]);
            updatedPixelsPostions.add(pixelsPositions[i]);
            updatedPixelsEdits.add(pixelsEdits[i]);
        }

        canvasPrintDTO.setPrint(print);
        // TODO : UPDATE INTERNAL STATE
        canvasPrints.put(canvasPrintDTO.getCanvasId(), canvasPrintDTO);
        return biggestPosIndex;
    }

    public void updateDatabase(Long canvasId, List<Long> updatedPixelsPostions, List<Long> updatedPixelsEdits, ConcurrentLinkedQueue<Runnable> dbUpdates) {
        Integer biggestPostFinal = Math.toIntExact(updatedPixelsPostions.get(0));
        for (int i = 1; i < updatedPixelsPostions.size(); i++) {
            if (updatedPixelsPostions.get(i) > biggestPostFinal) {
                biggestPostFinal = Math.toIntExact(updatedPixelsPostions.get(i));
            }
        }
        Integer finalBiggestPostFinal = biggestPostFinal;
        dbUpdates.add(() -> databaseService.updateDataBase(canvasId, finalBiggestPostFinal, updatedPixelsPostions, updatedPixelsEdits));
    }
}