package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanvasDTO {
    private String canvasName;
    private Boolean isPrivate;

    public void setCanvasName(String canvasName) {
        this.canvasName = canvasName;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public String getCanvasName() {
        return canvasName;
    }
}
