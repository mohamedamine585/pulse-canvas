package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrawEvent {
    private Long[] pixelsPositions;       // Representing the pixels as an int array
    private Long[] pixelsEdits; // Representing the edits as a byte array


    public Long[] getPixelsEdits() {
        return pixelsEdits;
    }

    public void setPixelsEdits(Long[] pixelsEdits) {
        this.pixelsEdits = pixelsEdits;
    }

    public void setPixelsPositions(Long[] pixelsPositions) {
        this.pixelsPositions = pixelsPositions;
    }

    public Long[] getPixelsPositions() {
        return pixelsPositions;
    }
}
