package com.pulse.canvas.entities;


import jakarta.persistence.*;

@Entity
public class CanvasPrintEditRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long canvasPrintEditRangeId;

    private Integer minIndx;
    private Integer maxIndx;
    @ManyToOne()
    private CanvasPrintEdit canvasEdit;

    public void setCanvasPrintEditRangeId(Long canvasPrintEditRangeId) {
        this.canvasPrintEditRangeId = canvasPrintEditRangeId;
    }

    public CanvasPrintEdit getCanvasEdit() {
        return canvasEdit;
    }

    public Integer getMaxIndx() {
        return maxIndx;
    }

    public void setCanvasEdit(CanvasPrintEdit canvasEdit) {
        this.canvasEdit = canvasEdit;
    }

    public void setMaxIndx(Integer maxIndx) {
        this.maxIndx = maxIndx;
    }

    public void setMinIndx(Integer minIndx) {
        this.minIndx = minIndx;
    }

    public Integer getMinIndx() {
        return minIndx;
    }

    public Long getCanvasPrintEditRangeId() {
        return canvasPrintEditRangeId;
    }


}
