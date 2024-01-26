package com.xwintop.xJavaFxTool.services.littleTools;

/**
 * 调整图片大小的方式
 */
public enum ResizeMode {

    Pixel("绝对尺寸(px)"), Percentage("相对原始图片比例(%)");

    private final String name;

    ResizeMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
