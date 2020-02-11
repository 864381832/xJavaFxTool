package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.services.index.PluginManageService;
import com.xwintop.xcore.util.ConfigureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

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
            File file = ConfigureUtil.getConfigureFile("systemConfigure.properties");
            PropertiesConfiguration xmlConfigure = new PropertiesConfiguration(file);
            String localeString = xmlConfigure.getString("Locale");
            if (StringUtils.isNotEmpty(localeString)) {
                String[] locale1 = localeString.split("_");
                Config.defaultLocale = new Locale(locale1[0], locale1[1]);
            }
        } catch (Exception e) {
            log.error("初始化本地语言失败", e);
        }
    }

    /**
     * @Title: loaderToolFxmlLoaderConfiguration
     * @Description: 加载本地工具
     */
//    public static List<ToolFxmlLoaderConfiguration> loaderToolFxmlLoaderConfiguration() {
//        List<ToolFxmlLoaderConfiguration> toolList = new ArrayList<ToolFxmlLoaderConfiguration>();
//        try {
//            XMLConfiguration xml = new XMLConfiguration("config/toolFxmlLoaderConfiguration.xml");
//            for (ConfigurationNode configurationNode : xml.getRoot().getChildren("ToolFxmlLoaderConfiguration")) {
//                ToolFxmlLoaderConfiguration toolFxmlLoaderConfiguration = new ToolFxmlLoaderConfiguration();
//                List<ConfigurationNode> attributes = configurationNode.getAttributes();
//                for (ConfigurationNode configuration : attributes) {
//                    BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
//                            configuration.getValue());
//                }
//                List<ConfigurationNode> childrenList = configurationNode.getChildren();
//                for (ConfigurationNode configuration : childrenList) {
//                    BeanUtils.copyProperty(toolFxmlLoaderConfiguration, configuration.getName(),
//                            configuration.getValue());
//                }
//                if (StringUtils.isEmpty(toolFxmlLoaderConfiguration.getMenuParentId())) {
//                    toolFxmlLoaderConfiguration.setMenuParentId("toolsMenu");
//                }
//                toolList.add(toolFxmlLoaderConfiguration);
//            }
//        } catch (Exception e) {
//            log.error("加载本地工具失败", e);
//        }
//        return toolList;
//    }

    /**
     * @Title: addJarByLibs
     * @Description: 添加libs中jar包到系统中
     */
    public static void addJarByLibs() {
        PluginManageService.reloadPluginJarList();
        try {
            // 系统类库路径
            File libPath = new File("libs/");
            // 获取所有的.jar和.zip文件
            File[] jarFiles = libPath.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (jarFiles != null) {
                for (File file : jarFiles) {
                    if (!PluginManageService.getPluginJarIsEnable(file.getName())) {
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
//            boolean accessible = method.isAccessible(); // 获取方法的访问权限
//            try {
//                if (accessible == false) {
            method.setAccessible(true); // 设置方法的访问权限
//                }
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL url = jarFile.toURI().toURL();
//                try {
            method.invoke(classLoader, url);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } finally {
//                method.setAccessible(accessible);
//            }
        } catch (Exception e) {
            log.error("添加libs中jar包到系统中异常:", e);
        }
    }
}
