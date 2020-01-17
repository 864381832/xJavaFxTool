package com.xwintop.xJavaFxTool.utils;

import java.io.File;

public class ConfigureUtil {
    public static String getConfigurePath() {
        return System.getProperty("user.home") + "/xJavaFxTool/";
    }

    public static String getConfigurePath(String fileName) {
        return getConfigurePath() + fileName;
    }

    public static File getConfigureFile(String fileName) {
        return new File(getConfigurePath(fileName));
    }
}
