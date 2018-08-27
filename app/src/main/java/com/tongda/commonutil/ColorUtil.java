package com.tongda.commonutil;



/**
 * Created by chang on 2016/5/2.
 */
public class ColorUtil {

    public static String color2Hex(int color) {
        String hexString = Integer.toHexString(color);
        return String.format("#%1$6s", hexString).replace(" ", "0").toUpperCase();
    }
}
