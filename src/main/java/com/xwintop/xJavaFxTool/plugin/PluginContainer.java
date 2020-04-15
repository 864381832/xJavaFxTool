package com.xwintop.xJavaFxTool.plugin;

import com.xwintop.xJavaFxTool.AppException;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import org.apache.commons.lang3.StringUtils;

/**
 * 插件运行容器，在单独的 ClassLoader 中运行插件，防止不同插件依赖库冲突
 */
public class PluginContainer {

    private final ClassLoader parentClassLoader;

    private PluginClassLoader pluginClassLoader;

    private final PluginJarInfo pluginJarInfo;

    public PluginContainer(ClassLoader parentClassLoader, PluginJarInfo pluginJarInfo) {
        this.parentClassLoader = parentClassLoader;
        this.pluginJarInfo = pluginJarInfo;
    }

    /**
     * 从 ClassLoader 中创建 FXMLLoader 对象
     */
    public FXMLLoader createFXMLLoader() {
        try {
            pluginClassLoader = new PluginClassLoader(parentClassLoader, pluginJarInfo.getFile());
            FXMLLoader pluginFxmlLoader = (FXMLLoader)
                pluginClassLoader.loadClass("javafx.fxml.FXMLLoader").newInstance();

            // 必须去掉前缀的 "/" 才能正确读取
            String fxmlPathFixed = StringUtils.removeStart(pluginJarInfo.getFxmlPath(), "/");

            URL resource = pluginClassLoader.getResource(fxmlPathFixed);
            if (resource == null) {
                FxAlerts.error("加载失败", "无法在插件 " + pluginJarInfo.getFile().getName() +
                    " 内找到所需资源 " + pluginJarInfo.getFxmlPath());
                return null;
            }

            pluginFxmlLoader.setLocation(resource);

            if (StringUtils.isNotEmpty(pluginJarInfo.getBundleName())) {
                ResourceBundle resourceBundle = ResourceBundle
                    .getBundle(pluginJarInfo.getBundleName(), Config.defaultLocale, pluginClassLoader);
                pluginFxmlLoader.setResources(resourceBundle);
            }
            return pluginFxmlLoader;
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    /**
     * 关闭 ClassLoader，不再加载新的类或资源。当其加载的所有对象都可以被回收时，ClassLoader 自身也可以被回收
     */
    public void unload() {
        try {
            this.pluginClassLoader.close();
        } catch (IOException e) {
            throw new AppException(e);
        }
    }
}
