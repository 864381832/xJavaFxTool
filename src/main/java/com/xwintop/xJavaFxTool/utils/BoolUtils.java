package com.xwintop.xJavaFxTool.utils;

public class BoolUtils {

    public static boolean is(Boolean b) {
        return b != null && b;
    }

    public static boolean isNot(Boolean b) {
        return !is(b);
    }
}
