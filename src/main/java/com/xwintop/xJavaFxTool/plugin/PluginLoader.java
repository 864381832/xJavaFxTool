package com.xwintop.xJavaFxTool.plugin;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;

@Slf4j
public class PluginLoader {
    /**
     * 以新 Tab 方式打开插件，但使用独立的 ClassLoader
     */
    public static Tab loadIsolatedPluginAsTab(PluginJarInfo plugin, TabPane tabPane, boolean singleWindowBoot) {
        try {
            PluginContainer pluginContainer = new PluginContainer(PluginLoader.class.getClassLoader(), plugin);
            WeakReference<PluginContainer> containerRef = new WeakReference<>(pluginContainer);
            FXMLLoader generatingCodeFXMLLoader = pluginContainer.createFXMLLoader();
            if (generatingCodeFXMLLoader == null) {
                return null;
            }

            if (singleWindowBoot) {
                JavaFxViewUtil.getNewStage(plugin.getTitle(), plugin.getIconPath(), generatingCodeFXMLLoader);
                return null;
            }

            Tab tab = new Tab(plugin.getTitle());
            if (StringUtils.isNotEmpty(plugin.getIconPath())) {
                ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
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

    public static Tab loadWebViewAsTab(PluginJarInfo plugin, TabPane tabPane, boolean singleWindowBoot) {
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        String url = plugin.getPagePath();
        String title = plugin.getTitle();

        if (url.startsWith("http")) {
            webEngine.load(url);
        } else {
            PluginContainer pluginContainer = new PluginContainer(plugin);
            webEngine.load(pluginContainer.getResource(url).toExternalForm());
        }

        if (singleWindowBoot) {
            JavaFxViewUtil.getNewStage(title, plugin.getIconPath(), new BorderPane(browser));
            return null;
        }
        Tab tab = new Tab(title);
        if (StringUtils.isNotEmpty(plugin.getIconPath())) {
            ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
            imageView.setFitHeight(18);
            imageView.setFitWidth(18);
            tab.setGraphic(imageView);
        }
        tab.setContent(browser);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        return tab;
    }
}
