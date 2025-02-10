package com.pulse.canvas.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "canvasp")
@Data
public class CanvasPrint {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @MapsId  // Uses the same ID as Canvas
    @JoinColumn(name = "id")  // Explicitly joins with the Canvas entity's id
    private Canvas canvas;

    @Lob  // Use for large binary data such as images
    private byte[] print;

    // Default constructor (required for JPA)
    public CanvasPrint() {}

    // Constructor with parameters
    public CanvasPrint(Canvas canvas, byte[] print) {
        this.canvas = canvas;
        this.print = print;
    }

    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for canvas
    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    // Getter and Setter for print (the binary data)
    public byte[] getPrint() {
        return print;
    }

    public void setPrint(byte[] print) {
        this.print = print;
    }
}
