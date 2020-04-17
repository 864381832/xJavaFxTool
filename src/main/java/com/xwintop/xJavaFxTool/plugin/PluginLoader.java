package com.xwintop.xJavaFxTool.plugin;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class PluginLoader {

    /**
     * 以新窗口方式打开插件
     */
    public static void loadPluginAsWindow(PluginJarInfo plugin) {
        try {
            FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(PluginLoader.class.getResource(plugin.getFxmlPath()));

            if (StringUtils.isNotEmpty(plugin.getBundleName())) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(plugin.getBundleName(), Config.defaultLocale);
                generatingCodeFXMLLoader.setResources(resourceBundle);
            }

            JavaFxViewUtil.getNewStage(plugin.getTitle(), plugin.getIconPath(), generatingCodeFXMLLoader);
        } catch (Exception e) {
            log.error("加载插件失败", e);
        }
    }

    /**
     * 以新 Tab 方式打开插件
     */
    public static Tab loadPluginAsTab(PluginJarInfo plugin, TabPane tabPane) {
        try {
            URL resource = PluginLoader.class.getResource(plugin.getFxmlPath());
            if (resource == null) {
                FxAlerts.error("加载插件失败", "无法读取资源文件 '" + plugin.getFxmlPath() + "'");
                return null;
            }

            FXMLLoader generatingCodeFXMLLoader = new FXMLLoader(resource);

            if (StringUtils.isNotEmpty(plugin.getBundleName())) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(plugin.getBundleName(), Config.defaultLocale);
                generatingCodeFXMLLoader.setResources(resourceBundle);
            }

            Tab tab = new Tab(plugin.getTitle());

            if (StringUtils.isNotEmpty(plugin.getIconPath())) {
                ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }

            tab.setContent(generatingCodeFXMLLoader.load());
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            tab.setOnCloseRequest(
                event -> JavaFxViewUtil.setControllerOnCloseRequest(generatingCodeFXMLLoader.getController(), event)
            );

            return tab;
        } catch (Exception e) {
            log.error("加载插件失败", e);
        }

        return null;
    }

    /**
     * 以新 Tab 方式打开插件，但使用独立的 ClassLoader
     */
    public static Tab loadIsolatedPluginAsTab(PluginJarInfo plugin, TabPane tabPane) {
        try {
            Tab tab = new Tab(plugin.getTitle());

            if (StringUtils.isNotEmpty(plugin.getIconPath())) {
                ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }

            PluginContainer pluginContainer = new PluginContainer(PluginLoader.class.getClassLoader(), plugin);
            WeakReference<PluginContainer> containerRef = new WeakReference<>(pluginContainer);
            FXMLLoader generatingCodeFXMLLoader = pluginContainer.createFXMLLoader();
            if (generatingCodeFXMLLoader == null) {
                return null;
            }

            Node root = generatingCodeFXMLLoader.load();
            Object controller = generatingCodeFXMLLoader.getController();
            WeakReference<Object> controllerRef = new WeakReference<>(controller);

            tab.setContent(root);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            tab.setOnCloseRequest(
                event -> {
                    Object ctrl = controllerRef.get();
                    if (ctrl != null) {
                        JavaFxViewUtil.setControllerOnCloseRequest(ctrl, event);
                    }

                    PluginContainer container = containerRef.get();
                    if (container != null) {
                        log.info("插件关闭：" + container.getPluginJarInfo().getName());
                        container.unload();
                    }
                }
            );

            return tab;
        } catch (Exception e) {
            log.error("加载插件失败", e);
            FxAlerts.error("插件加载失败", e);
        }

        return null;
    }

    public static Tab loadWebViewAsTab(PluginJarInfo plugin, TabPane tabPane) {
        PluginContainer pluginContainer = new PluginContainer(plugin);
        WebView browser = pluginContainer.createInstance(WebView.class);
        WebEngine webEngine = browser.getEngine();
        String url = plugin.getPagePath();
        String title = plugin.getTitle();

        if (url.startsWith("http")) {
            webEngine.load(url);
        } else {
            webEngine.load(pluginContainer.getResource(url).toExternalForm());
        }

        Tab tab = new Tab(title);
        tab.setContent(browser);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        return tab;
    }
}
