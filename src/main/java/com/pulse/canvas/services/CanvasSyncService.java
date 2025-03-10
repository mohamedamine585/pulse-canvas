package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Helper.AppEventTriggers.DrawEventToSync;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CanvasSyncService {

    @Autowired
    private String appInstanceId;

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    private KafkaTemplate<String, DrawEvent> kafkaTemplate;

    public CanvasSyncService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void sendCanvasToSync(DrawEvent drawEvent) {
        kafkaTemplate.send("canvas-sync", drawEvent);
    }

    @KafkaListener(topics = "canvas-sync")
    public void receiveCanvasSync(DrawEvent drawEvent) {

        if(drawEvent.getInstanceId().equals(appInstanceId)){
            return;
        }
        DrawEventToSync drawEventToSync = new DrawEventToSync(this);
        drawEventToSync.setDrawEvent(drawEvent);

        this.eventPublisher.publishEvent(drawEventToSync);
    }
}
