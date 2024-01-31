package com.xwintop.xJavaFxTool.services.games;

class CheckerBoard {
    //棋盘的一些常量
    static final int UNIT = 55;//一个棋格大小
    static final int LEFT_X = 30;//距左边界
    static final int RIGHT_X = LEFT_X + 8 * UNIT;//距右边界
    static final int TOP_Y = 30;//距上边界
    static final int BOTTOM_Y = TOP_Y + 9 * UNIT;//距下边界

    //将棋盘上的坐标转化为像素值
    static double xToPx(int x) {
        return LEFT_X + x * UNIT;
    }

    static double yToPx(int y) {
        return TOP_Y + y * UNIT;
    }

    //将像素值转化为最近的坐标值
    static int pxToX(double x_Px) {
        Double t = (x_Px - LEFT_X) / UNIT;
        long ans = Math.round(t);
        return (int) ans;
    }

    static int pxToY(double y_Px) {
        Double t = (y_Px - TOP_Y) / UNIT;
        long ans = Math.round(t);
        return (int) ans;
    }
}
