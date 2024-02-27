package com.xwintop.xJavaFxTool.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.xwintop.xcore.util.ConfigureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/*
 * 存取框架配置
 */
@Slf4j
public class Config {

    public static final String CONFIG_FILE_NAME = "systemConfigure.json";

    public static Locale defaultLocale = Locale.getDefault();// 设置系统语言

    public static final String xJavaFxToolVersions = "V0.4.0";// xJavaFxTool版本信息

    public enum Keys {
        MainWindowWidth, MainWindowHeight, MainWindowTop, MainWindowLeft,
        Locale, NotepadEnabled, RememberWindowLocation, ConfirmExit,
        NewLauncher
    }

    private static JSONObject conf;

    public static JSONObject getConfig() {
        try {
            if (conf == null) {
                conf = new JSONObject();
                File file = ConfigureUtil.getConfigureFile(CONFIG_FILE_NAME);
                if (file.exists()) {
                    conf = JSON.parseObject(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            log.error("加载本地配置失败：", e);
            // 即使加载失败，也要返回一个内存中的 PropertiesConfiguration 对象，以免程序报错。
            conf = new JSONObject();
        }

        return conf;
    }

    public static void saveConfig() {
        File file = ConfigureUtil.getConfigureFile(CONFIG_FILE_NAME);
        try {
            FileUtils.writeStringToFile(file, conf.toJSONString(JSONWriter.Feature.WriteMapNullValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改配置，修改后的值将会自动保存
     */
    public static void set(Keys key, Object value) {
        getConfig().put(key.name(), value);
        saveConfig();
    }

    public static String get(Keys key, String def) {
        return (String) getConfig().getOrDefault(key.name(), def);
    }

    public static double getDouble(Keys key, double def) {
        return (double) getConfig().getOrDefault(key.name(), def);
    }

    public static boolean getBoolean(Keys key, boolean def) {
        return (boolean) getConfig().getOrDefault(key.name(), def);
    }
}
