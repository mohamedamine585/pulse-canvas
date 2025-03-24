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
    @JoinColumn(name = "canvasEditId")
    private CanvasPrint canvasEdit;
}
