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
    ConcurrentHashMap<Integer,Integer> print;

    public CanvasPrintDTO(Long canvasId, Long id, ConcurrentHashMap<Integer,Integer> print) {
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

    public ConcurrentHashMap<Integer, Integer> getPrint() {
        return print;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }

    public void setPrint(ConcurrentHashMap<Integer,Integer> print) {
        this.print = print;
    }
}
