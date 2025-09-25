package com.pulse.canvas.Dtoes;

import com.pulse.canvas.enums.EventType;

import java.time.LocalDateTime;

public class LiveEventDTO {
    private EventType eventType;
    private Long timestamp;
    private Long canvasId;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getCanvasId() {
        return canvasId;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }
}
