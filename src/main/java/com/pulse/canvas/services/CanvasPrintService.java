package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.configurations.AppConfig;
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

    @Autowired
    private String  appInstanceId;

    @Async
    public void processUpdate(DrawEvent drawEvent, Map<Long, CanvasPrintDTO> canvasPrints, ConcurrentLinkedQueue<Runnable> dbUpdates,boolean shouldUpdateDatabase) {
        try {
            long biggestPost = 0;
            Long canvasId = drawEvent.getCanvasId();
            List<Long> updatedPixelsPostions = new ArrayList<>();
            List<Long> updatedPixelsEdits = new ArrayList<>();

            CanvasPrintDTO canvasPrintDTO = canvasPrints.get(canvasId);
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
                print.put(pixelsPositions[i], pixelsEdits[i]);
                updatedPixelsPostions.add(pixelsPositions[i]);
                updatedPixelsEdits.add(pixelsEdits[i]);
                if (pixelsPositions[i] > biggestPost) {
                    biggestPost = pixelsPositions[i];
                }
            }

            canvasPrintDTO.setPrint(print);
            canvasPrints.put(canvasPrintDTO.getCanvasId(), canvasPrintDTO);


            canvasSyncService.sendCanvasToSync(drawEvent);
            webSocketService.broadcastCanvasPrint(canvasId, MessageType.CANVAS_UPDATE, drawEvent.getSessionId() ,updatedPixelsPostions, updatedPixelsEdits);
            if (!shouldUpdateDatabase)
                return;
            updateDatabase(canvasId, updatedPixelsPostions, updatedPixelsEdits, dbUpdates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateDatabase(Long canvasId, List<Long> updatedPixelsPostions, List<Long> updatedPixelsEdits, ConcurrentLinkedQueue<Runnable> dbUpdates) {
         Integer biggestPostFinal = Math.toIntExact(updatedPixelsPostions.get(0));
        for(int i = 1; i < updatedPixelsPostions.size(); i++){
            if(updatedPixelsPostions.get(i) > biggestPostFinal){
                biggestPostFinal = Math.toIntExact(updatedPixelsPostions.get(i));
            }
        }
        Integer finalBiggestPostFinal = biggestPostFinal;
        dbUpdates.add(() -> databaseService.updateDataBase(canvasId, finalBiggestPostFinal, updatedPixelsPostions, updatedPixelsEdits));

    }
}