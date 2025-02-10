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
    Long printId;
    byte[] print;

    public CanvasPrintDTO(Long canvasId, Long id, byte[] print) {
        this.canvasId = canvasId;
        this.printId = id;
        this.print = print;
    }
    public Long getPrintId() {
        return printId;
    }

    public void setPrintId(Long printId) {
        this.printId = printId;
    }

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
