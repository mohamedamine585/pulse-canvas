package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CanvasPrintDTO {

    Long canvasId ;
    byte[] print;

    public Long getCanvasId() {
        return canvasId;
    }

    public byte[] getPrint() {
        return print;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }

    public void setPrint(byte[] print) {
        this.print = print;
    }
}
