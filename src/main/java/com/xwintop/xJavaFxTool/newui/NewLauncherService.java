package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * 新界面中负责与插件操作相关的逻辑
 */
public class NewLauncherService {

    private static final NewLauncherService instance = new NewLauncherService();

    private NewLauncherController newLauncherController;

    private Map<Tab, PluginJarInfo> jarInfoMap = new HashMap<>();

    private PluginItemController currentPluginItem;

    public static NewLauncherService getInstance() {
        return instance;
    }

    private NewLauncherService() {

    }

    public void setController(NewLauncherController newLauncherController) {
        this.newLauncherController = newLauncherController;
    }

    public void setCurrentPluginItem(PluginItemController currentPluginItem) {
        this.currentPluginItem = currentPluginItem;
    }

    public PluginItemController getCurrentPluginItem() {
        return currentPluginItem;
    }

    public void loadPlugin(PluginJarInfo pluginJarInfo) {
        TabPane tabPane = this.newLauncherController.getTabPane();

        for (Entry<Tab, PluginJarInfo> entry : jarInfoMap.entrySet()) {
            if (entry.getValue() == pluginJarInfo) {
                tabPane.getSelectionModel().select(entry.getKey());
                return;
            }
        }

        Tab tab = PluginLoader.loadPluginAsTab(pluginJarInfo, tabPane);
        if (tab != null) {
            tab.setOnClosed(event -> this.jarInfoMap.remove(tab));
            jarInfoMap.put(tab, pluginJarInfo);
        }
    }


}
