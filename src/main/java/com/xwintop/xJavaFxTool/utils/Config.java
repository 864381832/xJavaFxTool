package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xcore.util.ConfigureUtil;
import java.io.File;
import java.util.Locale;
import org.apache.commons.configuration.PropertiesConfiguration;

/*
 * 存取框架配置
 */
public class Config {

    public static final String CONFIG_FILE_NAME = "systemConfigure.properties";

    public static Locale defaultLocale = Locale.getDefault();// 设置系统语言

    public static final String xJavaFxToolVersions = "V0.2.3";// xJavaFxTool版本信息

    ///////////////////////////////////////////////////////////////

    public enum Keys {
        MainWindowWidth, MainWindowHeight, MainWindowTop, MainWindowLeft,
        Locale, NotepadEnabled, RememberWindowLocation, ConfirmExit,
        NewLauncher
    }

    private static PropertiesConfiguration conf;

    public static PropertiesConfiguration getConfig() {
        try {
            if (conf == null) {
                File file = ConfigureUtil.getConfigureFile(CONFIG_FILE_NAME);
                conf = new PropertiesConfiguration(file);
                conf.setAutoSave(true); // 启用自动保存
            } else {
                conf.reload();
            }
        } catch (Exception e) {
            e.printStackTrace();

            // 即使加载失败，也要返回一个内存中的 PropertiesConfiguration 对象，以免程序报错。
            conf = new PropertiesConfiguration();
        }

        return conf;
    }

    /**
     * 修改配置，修改后的值将会自动保存
     */
    public static void set(Keys key, Object value) {
        getConfig().setProperty(key.name(), value);
    }

    public static String get(Keys key, String def) {
        Object value = getConfig().getProperty(key.name());
        return value == null ? def : value.toString();
    }

    public static int getInt(Keys key, int def) {
        return Integer.parseInt(get(key, String.valueOf(def)));
    }

    public static double getDouble(Keys key, double def) {
        return Double.parseDouble(get(key, String.valueOf(def)));
    }

    public static long getLong(Keys key, long def) {
        return Long.parseLong(get(key, String.valueOf(def)));
    }

    public static boolean getBoolean(Keys key, boolean def) {
        return Boolean.parseBoolean(get(key, String.valueOf(def)));
    }
}
