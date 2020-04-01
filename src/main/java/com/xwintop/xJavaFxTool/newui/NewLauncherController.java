package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.Main;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class NewLauncherController {

    public VBox pluginCategories;

    public void openConfigDialog() {
        SystemSettingService.openSystemSettings("设置");
    }

    public void openPluginManager() {
        try {
            FXMLLoader fXMLLoader = PluginManageController.getFXMLLoader();
            Parent root = fXMLLoader.load();
            JavaFxViewUtil.openNewWindow(Main.RESOURCE_BUNDLE.getString("plugin_manage"), root);
        } catch (IOException e) {
            FxAlerts.error("打开插件管理对话框失败", e);
        }
    }

    public void initialize() {

        List<PluginJarInfo> pluginList = PluginManager.getInstance().getPluginList();
        ResourceBundle menuResourceBundle = Main.RESOURCE_BUNDLE;

        Map<String, PluginCategoryController> categoryControllers = new HashMap<>();

        for (PluginJarInfo jarInfo : pluginList) {
            String menuParentTitle = jarInfo.getMenuParentTitle();
            if (menuParentTitle != null) {

                String categoryName = menuResourceBundle.getString(menuParentTitle);
                String pluginName = jarInfo.getName();

                PluginCategoryController category = categoryControllers.computeIfAbsent(
                    categoryName, __ -> {
                        PluginCategoryController _category =
                            PluginCategoryController.newInstance(categoryName);
                        addCategory(_category);
                        return _category;
                    }
                );

                PluginItemController item = PluginItemController.newInstance(pluginName);
                category.addItem(item);
            }
        }
    }

    private void addCategory(PluginCategoryController category) {
        this.pluginCategories.getChildren().add(category.root);
    }
}
