package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.Main;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class NewLauncherController {

    public static final String FAVORITE_CATEGORY_NAME = "置顶";

    public VBox pluginCategories;

    public WebView startWebView;

    public TabPane tabPane;

    public TextField txtSearch;

    private ContextMenu itemContextMenu;

    // 实现搜索用
    private List<PluginItemController> pluginItemControllers = new ArrayList<>();

    public void initialize() {
        NewLauncherService.getInstance().setController(this);
        txtSearch.textProperty().addListener(this::onSearchKeywordChanged);
        initContextMenu();
        loadPlugins();  // 加载插件列表到界面上
        startWebView.getEngine().load(IndexController.QQ_URL); // 额外再打开一个反馈页面，可关闭
    }

    private void initContextMenu() {
        CheckMenuItem chkFavorite = new CheckMenuItem("置顶");
        chkFavorite.setStyle("-fx-padding: 0 35 0 0");

        this.itemContextMenu = new ContextMenu(chkFavorite);

        chkFavorite.setOnAction(event -> {
            CheckMenuItem _this = (CheckMenuItem) event.getSource();
            PluginItemController pluginItemController =
                NewLauncherService.getInstance().getCurrentPluginItem();
            setFavorite(pluginItemController, _this.isSelected());
        });
    }

    public void onSearchKeywordChanged(Observable ob, String _old, String keyword) {
        boolean notSearching = StringUtils.isBlank(keyword);
        pluginItemControllers.forEach(itemController -> {
            itemController.setVisible(notSearching || itemController.matchKeyword(keyword));
        });
    }

    private void setFavorite(PluginItemController itemController, boolean isFavorite) {
        if (itemController == null) {
            return;
        }

        itemController.getPluginJarInfo().setIsFavorite(isFavorite);
        PluginManager.getInstance().saveToFileQuietly();
        loadPlugins();
    }

    /**
     * 加载/刷新插件列表
     */
    private void loadPlugins() {

        this.pluginCategories.getChildren().clear();
        this.pluginItemControllers.clear();

        List<PluginJarInfo> pluginList = PluginManager.getInstance().getPluginList();
        ResourceBundle menuResourceBundle = Main.RESOURCE_BUNDLE;

        Map<String, PluginCategoryController> categoryControllers = new HashMap<>();

        for (PluginJarInfo jarInfo : pluginList) {
            String menuParentTitle = jarInfo.getMenuParentTitle();
            if (menuParentTitle != null) {

                String categoryName = jarInfo.getIsFavorite()?
                    FAVORITE_CATEGORY_NAME : menuResourceBundle.getString(menuParentTitle);

                PluginCategoryController category = categoryControllers.computeIfAbsent(
                    categoryName, __ -> {
                        PluginCategoryController _category =
                            PluginCategoryController.newInstance(categoryName);
                        addCategory(_category);
                        return _category;
                    }
                );

                PluginItemController item = PluginItemController.newInstance(jarInfo);
                item.setContextMenu(itemContextMenu);
                category.addItem(item);

                if (!pluginItemControllers.contains(item)) {
                    pluginItemControllers.add(item);
                }
            }
        }
    }

    private void addCategory(PluginCategoryController category) {
        if (category.lblCategoryName.getText().equals(FAVORITE_CATEGORY_NAME)) {
            this.pluginCategories.getChildren().add(0, category.root);
        } else {
            this.pluginCategories.getChildren().add(category.root);
        }
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

    public void openProjectUrl() {
        try {
            Desktop.getDesktop().browse(new URI("https://gitee.com/xwintop/xJavaFxTool"));
        } catch (Exception e) {
            log.error("打开项目地址失败", e);
        }
    }
}
