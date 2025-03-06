package com.pulse.canvas.Helper;

public class RGBAUtils {

    public static int encodeRGBA(int r, int g, int b, int a) {
        long pixelValue = (long) r << 24 | (long) g << 16 | (long) b << 8 | a; // Ensure unsigned 32-bit integer
        if(pixelValue > Integer.MAX_VALUE) {
            return  IntegerTransformers.transformLongToInt(pixelValue);
        }
        return (int) pixelValue;

    }
    public static byte[] decodeRGBA(long rgba) throws Exception {
        return new byte[]{
                ByteTransformer.pixelToByte((int) ((rgba >> 24) & 0xFF)), // Extract R
                ByteTransformer.pixelToByte((int) ((rgba >> 16) & 0xFF)), // Extract G
                ByteTransformer.pixelToByte((int) ((rgba >> 8) & 0xFF)),  // Extract B
                ByteTransformer.pixelToByte((int) (rgba & 0xFF) & 0xFF)   // Ensure A is positive
        };
    }


}
