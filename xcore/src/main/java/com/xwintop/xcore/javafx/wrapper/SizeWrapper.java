package com.xwintop.xcore.javafx.wrapper;

import javafx.scene.layout.Region;

/**
 * 指定一个大小，可反复用于界面元素上
 */
public class SizeWrapper {

    private double width;

    private double height;

    public static SizeWrapper of(double width, double height) {
        return new SizeWrapper(width, height);
    }

    private SizeWrapper(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public <T extends Region> T wrapPref(T region) {
        region.setPrefWidth(width);
        region.setPrefHeight(height);
        return region;
    }
}
