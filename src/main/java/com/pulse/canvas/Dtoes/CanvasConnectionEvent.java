package com.pulse.canvas.Dtoes;


import org.springframework.context.ApplicationEvent;

public class CanvasConnectionEvent extends ApplicationEvent {
    private final String sessionId;

    public CanvasConnectionEvent(Object source, String sessionId) {
        super(source);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
