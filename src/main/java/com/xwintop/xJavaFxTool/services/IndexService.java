package com.xwintop.xJavaFxTool.services;

import com.jpro.webapi.HTMLView;
import com.xwintop.xJavaFxTool.AppException;
import com.xwintop.xJavaFxTool.XJavaFxToolApplication;
import com.xwintop.xJavaFxTool.common.logback.ConsoleLogAppender;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginContainer;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        XJavaFxToolApplication.getStage().close();
        Platform.runLater(() -> {
            try {
                XJavaFxSystemUtil.initSystemLocal();    // 初始化本地语言
                new XJavaFxToolApplication().start(new Stage());
            } catch (Exception e) {
                log.error("设置本地语言失败！", e);
            }
        });
    }

    public void addNodepadAction(ActionEvent event) {
        addWebView(indexController.getBundle().getString("addNodepad"), "/web/monaco-editor-0.33.0/index.html", null);
    }

    public void addLogConsoleAction(ActionEvent event) {
        TextArea textArea = new TextArea();
        textArea.setFocusTraversable(true);
        ConsoleLogAppender.textAreaList.add(textArea);
        addTabAction(event, textArea, indexController.getBundle().getString("addLogConsole"), (Event event1) -> ConsoleLogAppender.textAreaList.remove(textArea));
    }

    public void addTabAction(ActionEvent event, Region content, String title, EventHandler closeRequest) {
        if (indexController.getSingleWindowBootCheckMenuItem().isSelected()) {
            Stage newStage = JavaFxViewUtil.getNewStage(title, null, content);
            if (closeRequest != null) {
                newStage.setOnCloseRequest(closeRequest);
            }
        } else {
            Tab tab = new Tab(title);
            tab.setContent(content);
            indexController.getTabPaneMain().getTabs().add(tab);
            if (event != null) {
                indexController.getTabPaneMain().getSelectionModel().select(tab);
            }
            if (closeRequest != null) {
                tab.setOnCloseRequest(event1 -> {
                    closeRequest.handle(event1);
                    indexController.getTabPaneMain().getSelectionModel().select(0);
                });
            }
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
            tab.setOnClosed(event -> {
                this.jarInfoMap.remove(pluginJarInfo);
                indexController.getTabPaneMain().getSelectionModel().select(0);
            });
            jarInfoMap.put(pluginJarInfo, tab);
        }
    }

    /**
     * 以新 Tab 方式打开插件，但使用独立的 ClassLoader
     */
    public static Tab loadIsolatedPluginAsTab(PluginJarInfo plugin, TabPane tabPane, boolean singleWindowBoot) {
        try {
            PluginContainer pluginContainer = new PluginContainer(plugin);
            FXMLLoader generatingCodeFXMLLoader = pluginContainer.createFXMLLoader();
            if (generatingCodeFXMLLoader == null) {
                return null;
            }

            if (singleWindowBoot) {
                JavaFxViewUtil.getNewStage(plugin.getTitle(), plugin.getIconPath(), generatingCodeFXMLLoader);
                return null;
            }

            Tab tab = new Tab(plugin.getTitle());
            if (plugin.getIconImage() == null) {
                if (StringUtils.isNotEmpty(plugin.getIconPath())) {
                    ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
                    imageView.setFitHeight(18);
                    imageView.setFitWidth(18);
                    tab.setGraphic(imageView);
                }
            } else {
                ImageView imageView = new ImageView(plugin.getIconImage());
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }

            Node root = generatingCodeFXMLLoader.load();

            tab.setContent(root);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            pluginContainer.setRootNode(root);
            pluginContainer.onPluginInitialized();
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
//        WebView browser = new WebView();
//        WebEngine webEngine = browser.getEngine();
        String url = plugin.getPagePath();
        String title = plugin.getTitle();

        HTMLView browser = null;
        if (url.startsWith("http")) {
//            webEngine.load(url);
            String contentIframe2 = "<iframe frameborder=\"0\" style=\"width: 100%; height: 100%;\" src=\"" + url + "\"> </iframe>";
            browser = new HTMLView(contentIframe2);
        } else {
            PluginContainer pluginContainer = new PluginContainer(plugin);
//            webEngine.load(pluginContainer.getResource(url).toExternalForm());
            try {
                browser = new HTMLView(IOUtils.toString(pluginContainer.getResource(url).openStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (singleWindowBoot) {
            JavaFxViewUtil.getNewStage(title, plugin.getIconPath(), new BorderPane(browser));
            return null;
        }
        Tab tab = new Tab(title);
        if (plugin.getIconImage() == null) {
            if (StringUtils.isNotEmpty(plugin.getIconPath())) {
                ImageView imageView = new ImageView(new Image(plugin.getIconPath()));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }
        } else {
            ImageView imageView = new ImageView(plugin.getIconImage());
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
