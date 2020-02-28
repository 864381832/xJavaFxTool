package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.services.index.PluginManageService;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: XJavaFxSystemUtil
 * @Description: javafx系统层工具类
 * @author: xufeng
 * @date: 2017年11月10日 下午4:35:17
 */
@Slf4j
public class XJavaFxSystemUtil {

    /**
     * @Title: initSystemLocal
     * @Description: 初始化本地语言
     */
    public static void initSystemLocal() {
        try {
            String localeString = Config.get(Config.Keys.Locale, "");
            if (StringUtils.isNotEmpty(localeString)) {
                String[] locale1 = localeString.split("_");
                Config.defaultLocale = new Locale(locale1[0], locale1[1]);
            }
        } catch (Exception e) {
            log.error("初始化本地语言失败", e);
        }
    }


    /**
     * @Title: addJarByLibs
     * @Description: 添加libs中jar包到系统中
     */
    public static void addJarByLibs() {
        PluginManager.getInstance().loadLocalPlugins();
        try {
            // 系统类库路径
            File libPath = new File("libs/");
            // 获取所有的.jar和.zip文件
            File[] jarFiles = libPath.listFiles(
                    (dir, name) -> name.endsWith(".jar")
            );
            if (jarFiles != null) {
                for (File file : jarFiles) {
                    if (!PluginManageService.isPluginEnabled(file.getName())) {
                        continue;
                    }
                    addJarClass(file);
                }
            }
        } catch (Exception e) {
            log.error("添加libs中jar包到系统中异常:", e);
        }
    }

    /**
     * @Title: addJarClass
     * @Description: 添加jar包到系统中
     */
    public static void addJarClass(File jarFile) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true); // 设置方法的访问权限
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL url = jarFile.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            log.error("添加libs中jar包到系统中异常:", e);
        }
    }
}
