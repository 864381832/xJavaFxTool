package com.xwintop.xJavaFxTool.utils;

import java.io.File;

public class ConfigureUtil {
	public static String getConfigurePath() {
		return System.getProperty("java.io.tmpdir") + "xJavaFxTool/";
	}

	public static String getConfigurePath(String fileName) {
		return System.getProperty("java.io.tmpdir") + "xJavaFxTool/" + fileName;
	}

	public static File getConfigureFile(String fileName) {
		return new File(getConfigurePath(fileName));
	}
}
