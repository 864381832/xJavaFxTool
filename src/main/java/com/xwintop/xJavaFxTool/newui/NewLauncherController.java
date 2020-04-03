package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.Main;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class NewLauncherController {

    public VBox pluginCategories;

    public WebView startWebView;

    public TabPane tabPane;

    public TextField txtSearch;

    // 实现搜索用
    private List<PluginItemController> pluginItemControllers = new ArrayList<>();

    public void initialize() {
        NewLauncherService.getInstance().setController(this);
        txtSearch.textProperty().addListener(this::onSearchKeywordChanged);
        loadPlugins();  // 加载插件列表到界面上
        startWebView.getEngine().load(IndexController.QQ_URL); // 额外再打开一个反馈页面，可关闭
    }

    public void onSearchKeywordChanged(Observable ob, String _old, String keyword) {
        boolean notSearching = StringUtils.isBlank(keyword);
        pluginItemControllers.forEach(itemController -> {
            itemController.setVisible(notSearching || itemController.matchKeyword(keyword));
        });
    }

    private void loadPlugins() {
        List<PluginJarInfo> pluginList = PluginManager.getInstance().getPluginList();
        ResourceBundle menuResourceBundle = Main.RESOURCE_BUNDLE;

        Map<String, PluginCategoryController> categoryControllers = new HashMap<>();

        for (PluginJarInfo jarInfo : pluginList) {
            String menuParentTitle = jarInfo.getMenuParentTitle();
            if (menuParentTitle != null) {
                String categoryName = menuResourceBundle.getString(menuParentTitle);
                PluginCategoryController category = categoryControllers.computeIfAbsent(
                    categoryName, __ -> {
                        PluginCategoryController _category =
                            PluginCategoryController.newInstance(categoryName);
                        addCategory(_category);
                        return _category;
                    }
                );

                PluginItemController item = PluginItemController.newInstance(jarInfo);
                category.addItem(item);

                if (!pluginItemControllers.contains(item)) {
                    pluginItemControllers.add(item);
                }
            }
        }
    }

    private void addCategory(PluginCategoryController category) {
        this.pluginCategories.getChildren().add(category.root);
    }

    public TabPane getTabPane() {
        return tabPane;
    }

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
}
