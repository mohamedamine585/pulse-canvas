package com.pulse.canvas.Helper;

import java.util.concurrent.ConcurrentHashMap;

public class PixelMapBuilder {

    public static ConcurrentHashMap<Long, Long> buildPixelMap(byte[] print) {
        ConcurrentHashMap<Long, Long> printMap = new ConcurrentHashMap<>();
        for (int i = 0; i < print.length - 3; i += 4) {
            int r = ByteTransformer.byteToPixel(print[i]);
            int g = ByteTransformer.byteToPixel(print[i + 1]);
            int b = ByteTransformer.byteToPixel(print[i + 2]);
            int a = ByteTransformer.byteToPixel(print[i + 3]);
            int pixel = RGBAUtils.encodeRGBA(r, g, b, a);
            printMap.putIfAbsent((long) i / 4, IntegerTransformers.transformIntToLong(pixel));
        }
        return printMap;
    }
}