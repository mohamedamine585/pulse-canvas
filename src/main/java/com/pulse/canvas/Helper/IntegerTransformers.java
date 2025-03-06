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
            return (int) -(bigInt - Integer.MAX_VALUE);
        }
    }

    // Reverse the transformation
    public static long transformIntToLong(int value) {
        if (value >= 0) {
            return value;
        } else {
            return - (long) value + Integer.MAX_VALUE;
        }
    }

    public static void main(String[] args) {

        // Testing transformLongToInt
        System.out.println(IntegerTransformers.transformLongToInt(0));
        System.out.println(IntegerTransformers.transformLongToInt(2_147_483_647L));
        System.out.println(IntegerTransformers.transformLongToInt(2_147_483_650L));
        System.out.println(IntegerTransformers.transformLongToInt(4_294_967_295L));

        // Testing transformIntToLong
        System.out.println(IntegerTransformers.transformIntToLong(0));
        System.out.println(IntegerTransformers.transformIntToLong(2_147_483_647));
        System.out.println(IntegerTransformers.transformIntToLong(-3));
        System.out.println(IntegerTransformers.transformIntToLong(-1));
    }
}
