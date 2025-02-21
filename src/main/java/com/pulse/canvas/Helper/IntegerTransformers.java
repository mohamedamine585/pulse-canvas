package com.pulse.canvas.Helper;

public class IntegerTransformers {
    // Convert long to int, using negative ints for larger values
    public static int transformLongToInt(long bigInt) {
        if (bigInt < 0 || bigInt > 4_294_967_295L) {
            throw new IllegalArgumentException("Value must be between 0 and 4,294,967,295.");
        }

        if (bigInt <= Integer.MAX_VALUE) {
            return (int) bigInt;
        } else {
            // Map to negative int space
            return (int) (bigInt - (Integer.MAX_VALUE + 1L)) - Integer.MAX_VALUE - 1;
        }
    }

    // Reverse the transformation
    public static long transformIntToLong(int value) {
        if (value >= 0) {
            return value;
        } else {
            return ((long) value + (Integer.MAX_VALUE + 1L) * 2);
        }
    }

}
