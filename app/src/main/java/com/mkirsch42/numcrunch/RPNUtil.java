package com.mkirsch42.numcrunch;

public final class RPNUtil {

    public static double trigIn(double in) {
        if(MainActivity.getCurrent().rad()) {
            return in;
        }
        return Math.toRadians(in);
    }

    public static double trigOut(double out) {
        if(MainActivity.getCurrent().rad()) {
            return out;
        }
        return Math.toDegrees(out);
    }

}
