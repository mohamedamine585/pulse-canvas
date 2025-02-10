package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrawEvent {
    private Long canvasId;
    private int[] pixelsPositions;       // Representing the pixels as an int array
    private byte[] pixelsEdits; // Representing the edits as a byte array

    // Getters and setters
    public Long getCanvasId() {
        return canvasId;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }

    public byte[] getPixelsEdits() {
        return pixelsEdits;
    }

    public void setPixelsEdits(byte[] pixelsEdits) {
        this.pixelsEdits = pixelsEdits;
    }

    public void setPixelsPositions(int[] pixelsPositions) {
        this.pixelsPositions = pixelsPositions;
    }

    public int[] getPixelsPositions() {
        return pixelsPositions;
    }
}
