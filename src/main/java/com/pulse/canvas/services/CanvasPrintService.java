package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.enums.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    @Async
    public void processUpdate(DrawEvent drawEvent, Map<Long, CanvasPrintDTO> canvasPrints, ConcurrentLinkedQueue<Runnable> dbUpdates) {
        try {
            List<Long> updatedPixelsPostions = new ArrayList<>();
            List<Long> updatedPixelsEdits = new ArrayList<>();

            // TODO : Process DrawEvent
            processDrawEvent(drawEvent, canvasPrints, updatedPixelsPostions, updatedPixelsEdits);

            // TODO : Sync with other instances
            canvasSyncService.sendCanvasToSync(drawEvent);
            // TODO : Broadcast to all clients
            webSocketService.broadcastCanvasPrint(drawEvent.getCanvasId(), MessageType.CANVAS_UPDATE, drawEvent.getSessionId(), updatedPixelsPostions, updatedPixelsEdits);

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
            processDrawEvent(drawEvent, canvasPrints, updatedPixelsPostions, updatedPixelsEdits);

            // TODO : Broadcast to all clients
            webSocketService.broadcastCanvasPrint(drawEvent.getCanvasId(), MessageType.CANVAS_UPDATE, drawEvent.getSessionId(), updatedPixelsPostions, updatedPixelsEdits);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDrawEvent(DrawEvent drawEvent, Map<Long, CanvasPrintDTO> canvasPrints, List<Long> updatedPixelsPostions, List<Long> updatedPixelsEdits) throws Exception {
        Long canvasId = drawEvent.getCanvasId();
        CanvasPrintDTO canvasPrintDTO = canvasPrints.get(canvasId);
        if (canvasPrintDTO == null) {
            return;
        }
        ConcurrentHashMap<Long, Long> print = canvasPrintDTO.getPrint();
        Long[] pixelsEdits = drawEvent.getPixelsEdits();
        Long[] pixelsPositions = drawEvent.getPixelsPositions();

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
            print.put(pixelsPositions[i], pixelsEdits[i]);
            updatedPixelsPostions.add(pixelsPositions[i]);
            updatedPixelsEdits.add(pixelsEdits[i]);
        }

        canvasPrintDTO.setPrint(print);
        // TODO : UPDATE INTERNAL STATE
        canvasPrints.put(canvasPrintDTO.getCanvasId(), canvasPrintDTO);
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