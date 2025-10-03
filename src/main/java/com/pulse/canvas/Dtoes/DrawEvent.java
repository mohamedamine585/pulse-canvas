package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class DrawEvent   {
    private Long[] pixelsPositions;       // Representing the pixels as an int array
    private Long[] pixelsEdits; // Representing the edits as a byte array
    private String sessionId;
    private LocalDateTime eventTimestamp;
    private Long userId;
    private Long canvasId;
    private String appInstanceId;


    public String getInstanceId() {
        return appInstanceId;
    }

    public void setInstanceId(String instanceId) {
        this.appInstanceId = instanceId;
    }
    public Long getCanvasId() {
        return canvasId;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getAppInstanceId() {
        return appInstanceId;
    }

    public void setAppInstanceId(String appInstanceId) {
        this.appInstanceId = appInstanceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long[] getPixelsEdits() {
        return pixelsEdits;
    }

    public void setPixelsEdits(Long[] pixelsEdits) {
        this.pixelsEdits = pixelsEdits;
    }

    public void setPixelsPositions(Long[] pixelsPositions) {
        this.pixelsPositions = pixelsPositions;
    }

    public Long[] getPixelsPositions() {
        return pixelsPositions;
    }
}
