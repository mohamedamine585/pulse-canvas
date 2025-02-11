package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrawEvent {
    private Integer[] pixelsPositions;       // Representing the pixels as an int array
    private Integer[] pixelsEdits; // Representing the edits as a byte array


    public Integer[] getPixelsEdits() {
        return pixelsEdits;
    }

    public void setPixelsEdits(Integer[] pixelsEdits) {
        this.pixelsEdits = pixelsEdits;
    }

    public void setPixelsPositions(Integer[] pixelsPositions) {
        this.pixelsPositions = pixelsPositions;
    }

    public Integer[] getPixelsPositions() {
        return pixelsPositions;
    }
}
