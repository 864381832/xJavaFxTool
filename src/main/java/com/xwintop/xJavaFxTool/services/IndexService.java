package com.xwintop.xJavaFxTool.services;

import com.xwintop.xJavaFxTool.AppException;
import com.xwintop.xJavaFxTool.common.logback.ConsoleLogAppender;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginLoader;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
        FxAlerts.info("", indexController.getBundle().getString("SetLanguageText"));
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
            tab = PluginLoader.loadIsolatedPluginAsTab(pluginJarInfo, indexController.getTabPaneMain(), indexController.getSingleWindowBootCheckMenuItem().isSelected());
        } else if (controllerType.equals("WebView")) {
            tab = PluginLoader.loadWebViewAsTab(pluginJarInfo, indexController.getTabPaneMain(), indexController.getSingleWindowBootCheckMenuItem().isSelected());
        } else {
            throw new AppException("找不到 controllerType=" + controllerType + " 的加载方式");
        }

        if (tab != null) {
            tab.setOnClosed(event -> this.jarInfoMap.remove(pluginJarInfo));
            jarInfoMap.put(pluginJarInfo, tab);
        }
    }
}
