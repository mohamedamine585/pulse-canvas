package com.pulse.canvas.Helper;

public class RGBAUtils {

    public static int encodeRGBA(int r, int g, int b, int a) {
        return ((r << 24) | (g << 16) | (b << 8) | a); // Ensure unsigned 32-bit integer
    }

    public static int[] decodeRGBA(long rgba) {
        return new int[]{
                (int) (rgba >> 24) & 0xFF, // Extract R
                (int) (rgba >> 16) & 0xFF, // Extract G
                (int) (rgba >> 8) & 0xFF,  // Extract B
                (int) rgba & 0xFF          // Extract A
        };
    }

}
