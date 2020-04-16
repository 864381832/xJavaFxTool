package com.xwintop.xJavaFxTool.newui;

import com.xwintop.xJavaFxTool.Main;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.event.AppEvents;
import com.xwintop.xJavaFxTool.event.PluginEvent;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.newui.creator.CreatePluginProjectService;
import com.xwintop.xJavaFxTool.newui.creator.PluginProjectInfo;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.plugin.PluginParser;
import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.javafx.dialog.FxDialog;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class NewLauncherController {

    public static final String FAVORITE_CATEGORY_NAME = "置顶";

    public VBox pluginCategories;

    public WebView startWebView;

    public TabPane tabPane;

    public TextField txtSearch;

    public Hyperlink lnkCreatePlugin;

    private ContextMenu itemContextMenu;

    // 实现搜索用
    private final List<PluginItemController> pluginItemControllers = new ArrayList<>();

    private final Map<String, PluginCategoryController> categoryControllers = new HashMap<>();

    public void initialize() {
        NewLauncherService.getInstance().setController(this);
        txtSearch.textProperty().addListener(this::onSearchKeywordChanged);

        initContextMenu();
        loadPlugins();  // 加载插件列表到界面上

        startWebView.getEngine().load(IndexController.QQ_URL); // 额外再打开一个反馈页面，可关闭

        AppEvents.addEventHandler(PluginEvent.PLUGIN_DOWNLOADED, pluginEvent -> {
            PluginJarInfo pluginJarInfo = pluginEvent.getPluginJarInfo();
            PluginParser.parse(pluginJarInfo.getFile(), pluginJarInfo);
            loadPlugins();
        });
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
        this.categoryControllers.clear();

        List<PluginJarInfo> pluginList = PluginManager.getInstance().getPluginList();
        pluginList.forEach(this::loadPlugin);
    }

    public void loadPlugin(PluginJarInfo jarInfo) {
        String menuParentTitle = jarInfo.getMenuParentTitle();
        if (menuParentTitle != null) {

            String categoryName = jarInfo.getIsFavorite() ?
                FAVORITE_CATEGORY_NAME : Main.RESOURCE_BUNDLE.getString(menuParentTitle);

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

    public void openPluginCreator() {

        FxDialog<PluginCreatorController> dialog = new FxDialog<PluginCreatorController>()
            .setTitle("创建自己的插件")
            .setBodyFxml("/com/xwintop/xJavaFxTool/fxmlView/newui/plugin-creator.fxml")
            .setOwner(FxApp.primaryStage)
            .setResizable(true)
            .setButtonTypes(ButtonType.OK, ButtonType.CANCEL);

        PluginCreatorController controller = dialog.show();

        dialog
            .setButtonHandler(ButtonType.OK, (actionEvent, stage) -> {
                if (controller.isStartCreation()) {
                    try {
                        PluginProjectInfo info = controller.getPluginProjectInfo();
                        CreatePluginProjectService.getInstance().createProject(info);
                        FxAlerts.info("创建成功", "项目 '" + info.getArtifactId() + "' 已经创建完毕。");
                        Desktop.getDesktop().open(new File(info.getLocation()));
                    } catch (IOException e) {
                        FxAlerts.error("打开目标文件夹失败", e);
                    }
                }
                stage.close();
            })
            .setButtonHandler(ButtonType.CANCEL, (actionEvent, stage) -> stage.close());
    }
}
