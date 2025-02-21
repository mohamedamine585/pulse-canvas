package com.pulse.canvas.Helper;

public class RGBAUtils {

    public static int encodeRGBA(int r, int g, int b, int a) {
        return ((r << 24) | (g << 16) | (b << 8) | a); // Ensure unsigned 32-bit integer
    }

    public static int[] decodeRGBA(int rgba) {
        return new int[]{
                (rgba >> 24) & 0xFF, // Extract R
                (rgba >> 16) & 0xFF, // Extract G
                (rgba >> 8) & 0xFF,  // Extract B
                rgba & 0xFF          // Extract A
        };
    }

    public static void main(String[] args) {
        int encoded = encodeRGBA(255, 128, 64, 32);
        System.out.println("Encoded: " + encoded);

        int[] decoded = decodeRGBA(encoded);
        System.out.println("Decoded: R=" + decoded[0] + ", G=" + decoded[1] + ", B=" + decoded[2] + ", A=" + decoded[3]);
    }
}
