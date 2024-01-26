package com.xwintop.xJavaFxTool.services.littleTools;

public enum OutputFormats {

    ORIGINAL("原始格式", null),
    BMP("位图", "bmp"),
    PNG("便携式网络图形", "png"),
    GIF("图像互换格式", "gif"),
    JPEG("联合图像专家小组", "jpg");

    private final String name;

    private final String ext;

    OutputFormats(String name, String ext) {
        this.name = name;
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public String getExt() {
        return ext;
    }
}
