package com.xwintop.xcore.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ConfigureUtil {
    private static Map<String, Map> configCache = new HashMap<>();

    public static String getConfigurePath() {
        return System.getProperty("user.home") + "/xJavaFxTool/";
    }

    public static String getConfigurePath(String fileName) {
        return getConfigurePath() + fileName;
    }

    public static File getConfigureFile(String fileName) {
        return new File(getConfigurePath(fileName));
    }

    public static Map getConfig(File file) {
        Map conf = configCache.get(file.getAbsolutePath());
        try {
            if (conf == null) {
                conf = new JSONObject();
                if (file.exists()) {
                    conf = JSON.parseObject(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                }
                configCache.put(file.getAbsolutePath(), conf);
            }
        } catch (Exception e) {
            log.error("加载本地配置失败：", e);
            // 即使加载失败，也要返回一个内存中的 PropertiesConfiguration 对象，以免程序报错。
            conf = new JSONObject();
        }
        return conf;
    }

    public static Map getConfig(String fileName) {
        return getConfig(ConfigureUtil.getConfigureFile(fileName));
    }

    public static void saveConfig(String fileName) {
        saveConfig(ConfigureUtil.getConfigureFile(fileName));
    }

    public static void saveConfig(File file) {
        try {
            FileUtils.writeStringToFile(file, JSON.toJSONString(configCache.get(file.getAbsolutePath()), JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(String fileName, String key, Object value) {
        set(ConfigureUtil.getConfigureFile(fileName), key, value);
    }

    public static void set(File file, String key, Object value) {
        getConfig(file).put(key, value);
        saveConfig(file);
    }

    public static Object get(String fileName, String key) {
        return getConfig(fileName).get(key);
    }

    public static Object get(File file, String key) {
        return getConfig(file).get(key);
    }

    public static Object getOrDefault(String fileName, String key, Object def) {
        return getConfig(fileName).getOrDefault(key, def);
    }

    public static Object getOrDefault(File file, String key, Object def) {
        return getConfig(file).getOrDefault(key, def);
    }

    public static Double getDouble(String fileName, String key) {
        return ((JSONObject) getConfig(fileName)).getDouble(key);
    }

    public static Double getDoubleOrDefault(String fileName, String key, Double def) {
        Double value = getDouble(fileName, key);
        return value != null ? value : def;
    }
}
