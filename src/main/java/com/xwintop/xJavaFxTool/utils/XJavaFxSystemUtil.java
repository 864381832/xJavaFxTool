package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.Main;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.services.index.PluginManageService;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
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

            Main.RESOURCE_BUNDLE = ResourceBundle.getBundle("locale.Menu", Config.defaultLocale);
        } catch (Exception e) {
            log.error("初始化本地语言失败", e);
        }
    }


    /**
     * @Title: addJarByLibs
     * @Description: 添加libs中jar包到系统中
     */
    public static void addJarByLibs() {
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
            PluginManager.getInstance().loadLocalPlugins();
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
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            URL url = jarFile.toURI().toURL();
            if (classLoader instanceof URLClassLoader) {
                System.out.println("DEB: classLoader instanceof URLClassLoader");
                URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class sysclass = URLClassLoader.class;
                try {
                    Method method = sysclass.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(sysloader, url);
                } catch (Exception var5) {
                    var5.printStackTrace();
                    throw new IllegalStateException(var5.getMessage(), var5);
                }
            } else {
                try {
                    Field field = classLoader.getClass().getDeclaredField("ucp");
                    field.setAccessible(true);
                    Object ucp = field.get(classLoader);

                    System.out.println("DEB: invoke method!");
                    Method method = ucp.getClass().getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);

                    method.invoke(ucp, url);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw new IllegalStateException(exception.getMessage(), exception);
                }
            }
        } catch (Exception e) {
            log.error("添加libs中jar包到系统中异常:", e);
        }
    }
}
