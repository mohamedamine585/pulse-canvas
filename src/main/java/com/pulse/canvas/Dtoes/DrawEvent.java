package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrawEvent {
    private int canvasId;
    private int[] pixels;       // Representing the pixels as an int array
    private byte[] pixelsEdits; // Representing the edits as a byte array
}
