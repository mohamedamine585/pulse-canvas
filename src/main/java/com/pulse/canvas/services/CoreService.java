package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.CanvasPrintDTO;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Helper.AppEventTriggers.DrawEventToSync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class CoreService {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private CanvasPrintService canvasPrintService;

    private static final Map<Long, CanvasPrintDTO> canvasPrints = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<Runnable> dbUpdates = new ConcurrentLinkedQueue<>();
    private static final Map<Long, Timestamp> latestUpdates = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 5000)
    public void persistUpdates() {
        Runnable task;
        while ((task = dbUpdates.poll()) != null) {
            task.run();
        }
    }

    @Transactional
    public void addClient(WebSocketSession session) throws Exception {
        webSocketService.addClient(session, canvasPrints);
    }

    public void removeClient(WebSocketSession session) {
        webSocketService.removeClient(session);
    }

    public void processCanvasSyncUpdate(DrawEvent drawEvent) {
        canvasPrintService.processSyncUpdate(drawEvent, canvasPrints);
    }
    public void processCanvasUpdate(DrawEvent drawEvent) {

        canvasPrintService.processUpdate(drawEvent, canvasPrints, dbUpdates);
    }

    @EventListener
    public void onSyncCanvas(DrawEventToSync drawEventToSync) {
        System.out.println("Syncing canvas");
        processCanvasSyncUpdate(drawEventToSync.getDrawEvent());
    }


}