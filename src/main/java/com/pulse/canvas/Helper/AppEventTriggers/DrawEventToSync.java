package com.pulse.canvas.Helper.AppEventTriggers;

import com.pulse.canvas.Dtoes.DrawEvent;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Builder
public class DrawEventToSync extends ApplicationEvent {

    private DrawEvent drawEvent;

    public DrawEventToSync(Object source) {
        super(source);
    }

    public void setDrawEvent(DrawEvent drawEvent) {
        this.drawEvent = drawEvent;
    }

    public DrawEvent getDrawEvent() {
        return drawEvent;
    }
}
