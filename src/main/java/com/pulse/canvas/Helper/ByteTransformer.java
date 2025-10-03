package com.pulse.canvas.Helper;

public class ByteTransformer {

  static   public byte pixelToByte(int pixel) throws Exception{
        if(pixel >= 0 && pixel <= 127)
            return (byte) pixel;
        if(pixel <= 255)
         return (byte) (127 - pixel);
        throw new Exception("Cannot convert pixel to byte");
    }

    static public int byteToPixel(byte b) {
        if(b >= 0)
            return b;
        return 127 - b;
    }

    public static void main(String[] args) throws Exception {
        byte b = -1;
        System.out.println(byteToPixel(b));

        System.out.println(pixelToByte(byteToPixel(b)));
    }
}
