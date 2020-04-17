package com.xwintop.xJavaFxTool.newui;

import static com.xwintop.xJavaFxTool.utils.BoolUtils.isNot;

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
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.Observable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
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

    /**
     * 加载单个插件到界面，要求插件已经经过 {@link PluginParser#parse(File, PluginJarInfo)} 解析
     *
     * @param jarInfo 插件信息
     */
    private void loadPlugin(PluginJarInfo jarInfo) {

        if (!jarInfo.getFile().exists()) {
            log.info("跳过插件 {}: 文件不存在", jarInfo.getName());
            return;
        }

        if (isNot(jarInfo.getIsEnable())) {
            log.info("跳过插件 {}: 插件未启用", jarInfo.getName());
            return;
        }

        String menuParentTitle = jarInfo.getMenuParentTitle();
        if (menuParentTitle == null) {
            log.info("跳过插件 {}: menuParentTitle 为空", jarInfo.getName());
            return;
        }

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
        new FxDialog<PluginManageController>()
            .setBodyFxml(PluginManageController.FXML)
            .setOwner(FxApp.primaryStage)
            .setResizable(true)
            .setTitle(Main.RESOURCE_BUNDLE.getString("plugin_manage"))
            .setPrefWidth(800)
            .show();
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
