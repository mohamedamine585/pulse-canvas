package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@NoArgsConstructor
public class CanvasPrintDTO {

    Long canvasId ;
    Long printId;
    ConcurrentHashMap<Long,Long> print;

    public CanvasPrintDTO(Long canvasId, Long id, ConcurrentHashMap<Long,Long> print) {
        this.canvasId = canvasId;
        this.printId = id;
        if(print == null){
            this.print = new ConcurrentHashMap<>();
        }else{
            this.print = print;
        }
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

    public ConcurrentHashMap<Long, Long> getPrint() {
        return print;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }

    public void setPrint(ConcurrentHashMap<Long,Long> print) {
        this.print = print;
    }
}
