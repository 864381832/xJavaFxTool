package com.xwintop.xJavaFxTool.services.littleTools;

import java.io.File;

/**
 * 能处理的图片类型
 */
public enum AcceptableFormats {

    JPG("jpg", "jpeg"),
    PNG("png"),
    BMP("bmp"),
    GIF("gif");

    private final String[] extensions;

    AcceptableFormats(String... extensions) {
        this.extensions = extensions;
    }

    public static boolean isAcceptable(File file) {
        if (!file.isFile()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();
        for (AcceptableFormats f : values()) {
            for (String ext : f.extensions) {
                if (fileName.endsWith("." + ext)) {
                    return true;
                }
            }
        }

        return false;
    }
}
