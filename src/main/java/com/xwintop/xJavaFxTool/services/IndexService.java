package com.xwintop.xJavaFxTool.services;

import com.xwintop.xJavaFxTool.AppException;
import com.xwintop.xJavaFxTool.common.logback.ConsoleLogAppender;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginClassLoader;
import com.xwintop.xJavaFxTool.plugin.PluginContainer;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
@Slf4j
public class IndexService {
    private IndexController indexController;

    private Map<PluginJarInfo, Tab> jarInfoMap = new HashMap<>();

    public IndexService(IndexController indexController) {
        this.indexController = indexController;
    }

    public void setLanguageAction(String languageType) throws Exception {
        if ("简体中文".equals(languageType)) {
            Config.set(Config.Keys.Locale, Locale.SIMPLIFIED_CHINESE);
        } else if ("English".equals(languageType)) {
            Config.set(Config.Keys.Locale, Locale.US);
        }
        AlertUtil.showInfoAlert(indexController.getBundle().getString("SetLanguageText"));
    }

    public void addNodepadAction(ActionEvent event) {
        TextArea notepad = new TextArea();
        if (indexController.getSingleWindowBootCheckMenuItem().isSelected()) {
            JavaFxViewUtil.getNewStage(indexController.getBundle().getString("addNodepad"), null, notepad);
        } else {
            Tab tab = new Tab(indexController.getBundle().getString("addNodepad"));
            tab.setContent(notepad);
            indexController.getTabPaneMain().getTabs().add(tab);
            if (event != null) {
                indexController.getTabPaneMain().getSelectionModel().select(tab);
            }
        }
    }

    public void addLogConsoleAction(ActionEvent event) {
        TextArea textArea = new TextArea();
        textArea.setFocusTraversable(true);
        ConsoleLogAppender.textAreaList.add(textArea);
        if (indexController.getSingleWindowBootCheckMenuItem().isSelected()) {
            Stage newStage = JavaFxViewUtil.getNewStage(indexController.getBundle().getString("addLogConsole"), null, textArea);
            newStage.setOnCloseRequest(event1 -> {
                ConsoleLogAppender.textAreaList.remove(textArea);
            });
        } else {
            Tab tab = new Tab(indexController.getBundle().getString("addLogConsole"));
            tab.setContent(textArea);
            indexController.getTabPaneMain().getTabs().add(tab);
            if (event != null) {
                indexController.getTabPaneMain().getSelectionModel().select(tab);
            }
            tab.setOnCloseRequest((Event event1) -> {
                ConsoleLogAppender.textAreaList.remove(textArea);
            });
        }
    }

    /**
     * @Title: addWebView
     * @Description: 添加WebView视图
     */
    public void addWebView(String title, String url, String iconPath) {
        PluginJarInfo pluginJarInfo = new PluginJarInfo();
        pluginJarInfo.setTitle(title);
        pluginJarInfo.setIconPath(iconPath);
        pluginJarInfo.setPagePath(url);
        pluginJarInfo.setControllerType("WebView");
        loadPlugin(pluginJarInfo);
    }

    public void loadPlugin(PluginJarInfo pluginJarInfo) {
        if (!indexController.getSingleInstanceBootCheckMenuItem().isSelected() && !indexController.getSingleWindowBootCheckMenuItem().isSelected()) {
            if (jarInfoMap.containsKey(pluginJarInfo)) {
                indexController.getTabPaneMain().getSelectionModel().select(jarInfoMap.get(pluginJarInfo));
                return;
            }
        }
        Tab tab = null;
        String controllerType = pluginJarInfo.getControllerType();

        if (controllerType.equals("Node")) {
            tab = loadIsolatedPluginAsTab(pluginJarInfo, indexController.getTabPaneMain(), indexController.getSingleWindowBootCheckMenuItem().isSelected());
        } else if (controllerType.equals("WebView")) {
            tab = loadWebViewAsTab(pluginJarInfo, indexController.getTabPaneMain(), indexController.getSingleWindowBootCheckMenuItem().isSelected());
        } else {
            throw new AppException("找不到 controllerType=" + controllerType + " 的加载方式");
        }

        if (tab != null) {
            tab.setOnClosed(event -> this.jarInfoMap.remove(pluginJarInfo));
            jarInfoMap.put(pluginJarInfo, tab);
        }
    }

    /**
     * 以新 Tab 方式打开插件，但使用独立的 ClassLoader
     */
    public static Tab loadIsolatedPluginAsTab(PluginJarInfo plugin, TabPane tabPane, boolean singleWindowBoot) {
        try {
            PluginContainer pluginContainer = new PluginContainer(PluginClassLoader.class.getClassLoader(), plugin);
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

            tab.setContent(root);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            tab.setOnCloseRequest(
                event -> {
                    JavaFxViewUtil.setControllerOnCloseRequest(generatingCodeFXMLLoader.getController(), event);
                    log.info("插件关闭：" + pluginContainer.getPluginJarInfo().getName());
                    pluginContainer.unload();
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
