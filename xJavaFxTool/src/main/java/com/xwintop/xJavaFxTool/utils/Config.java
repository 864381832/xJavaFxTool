package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xcore.util.ConfigureUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

/*
 * 存取框架配置
 */
@Slf4j
public class Config {

    public static final String CONFIG_FILE_NAME = "systemConfigure.json";

    public static Locale defaultLocale = Locale.getDefault();// 设置系统语言

    public static final String xJavaFxToolVersions = "V1.0.0";// xJavaFxTool版本信息

    public enum Keys {
        MainWindowWidth, MainWindowHeight, MainWindowTop, MainWindowLeft,
        Locale, NotepadEnabled, RememberWindowLocation, ConfirmExit,
        NewLauncher
    }

    /**
     * 修改配置，修改后的值将会自动保存
     */
    public static void set(Keys key, Object value) {
        ConfigureUtil.set(CONFIG_FILE_NAME, key.name(), value);
    }

    public static String get(Keys key, String def) {
        return (String) ConfigureUtil.getOrDefault(CONFIG_FILE_NAME, key.name(), def);
    }

    public static double getDouble(Keys key, double def) {
        return ConfigureUtil.getDoubleOrDefault(CONFIG_FILE_NAME, key.name(), def);
    }

    public static boolean getBoolean(Keys key, boolean def) {
        return (boolean) ConfigureUtil.getOrDefault(CONFIG_FILE_NAME, key.name(), def);
    }
}
