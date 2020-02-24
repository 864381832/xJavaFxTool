package com.xwintop.xJavaFxTool.utils;

import java.util.Locale;

/*
 * 配置文件
 */
public class Config {

    public static Locale defaultLocale = Locale.getDefault();// 设置系统语言

    public static final String xJavaFxToolVersions = "V0.2.0";// xJavaFxTool版本信息

    public static final int xJavaFxToolVersionsInteger = 12;// xJavaFxTool更新信息

	/**
	 * 集中存放配置 key 的地方
	 */
    public static class Keys {

		/**
		 * 窗体相关配置
		 */
		public static class MainWindow {

            public static final String WIDTH = "main-window.width";

            public static final String HEIGHT = "main-window.height";

            public static final String TOP = "main-window.top";

            public static final String LEFT = "main-window.left";
        }
    }
}
