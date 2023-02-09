package com.xwintop.xJavaFxTool.controller;

import com.xwintop.xJavaFxTool.XJavaFxToolApplication;
import com.xwintop.xJavaFxTool.controller.index.PluginManageController;
import com.xwintop.xJavaFxTool.event.AppEvents;
import com.xwintop.xJavaFxTool.event.PluginEvent;
import com.xwintop.xJavaFxTool.model.PluginJarInfo;
import com.xwintop.xJavaFxTool.controller.plugin.PluginCategoryController;
import com.xwintop.xJavaFxTool.controller.plugin.PluginItemController;
import com.xwintop.xJavaFxTool.plugin.PluginManager;
import com.xwintop.xJavaFxTool.plugin.PluginParser;
import com.xwintop.xJavaFxTool.services.IndexService;
import com.xwintop.xJavaFxTool.services.index.SystemSettingService;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.VersionChecker;
import com.xwintop.xJavaFxTool.view.IndexView;
import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.javafx.dialog.FxDialog;
import com.xwintop.xcore.util.ConfigureUtil;
import com.xwintop.xcore.util.HttpClientUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @ClassName: IndexController
 * @Description: 主页
 * @author: xufeng
 * @date: 2017年7月20日 下午1:50:00
 */
@Slf4j
@Getter
@Setter
public class IndexController extends IndexView {
    public static final String QQ_URL = "https://support.qq.com/product/127577";

    public static final String STATISTICS_URL = "https://xwintop.gitee.io/maven/tongji/xJavaFxTool.html";

    public static final String FAVORITE_CATEGORY_NAME = XJavaFxToolApplication.RESOURCE_BUNDLE.getString("favoriteCategory");

    private Map<String, MenuItem> menuItemMap = new HashMap<>();

    private IndexService indexService = new IndexService(this);

    // 实现搜索用
    private List<PluginItemController> pluginItemControllers = new ArrayList<>();

    private Map<String, PluginCategoryController> categoryControllers = new HashMap<>();

    public static FXMLLoader getFXMLLoader() {
        URL url = IndexController.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/Index.fxml");
        return new FXMLLoader(url, XJavaFxToolApplication.RESOURCE_BUNDLE);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        if (Config.getBoolean(Config.Keys.NotepadEnabled, false)) {
            addNodepadAction(null);
        }
        this.indexService.addWebView(XJavaFxToolApplication.RESOURCE_BUNDLE.getString("feedback"), QQ_URL, null);
//        this.tongjiWebView.getEngine().load(STATISTICS_URL);
        this.tabPaneMain.getSelectionModel().select(0);
    }

    private void initEvent() {
        mainMenuBar.setUseSystemMenuBar(true);
        myTextField.textProperty().addListener((observable, oldValue, newValue) -> selectAction(newValue));
    }

    private void initService() {
        PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.loadLocalDevPluginConfiguration();
        loadPlugins();  // 加载插件列表到界面上
        AppEvents.addEventHandler(PluginEvent.PLUGIN_DOWNLOADED, pluginEvent -> loadPlugins());
    }

    /**
     * 加载/刷新插件列表
     */
    public void loadPlugins() {
        this.pluginCategories.getChildren().clear();
        this.pluginItemControllers.clear();
        this.categoryControllers.clear();
        this.menuItemMap.clear();
        this.moreToolsMenu.getItems().clear();

        PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.loadLocalPlugins();
        pluginManager.getEnabledPluginList().forEach(this::loadPlugin);
        pluginManager.getDevPluginList().forEach(this::loadPlugin);
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
        if (BooleanUtils.isFalse(jarInfo.getIsEnable())) {
            log.info("跳过插件 {}: 插件未启用", jarInfo.getName());
            return;
        }
        String menuParentTitle = jarInfo.getMenuParentTitle();
        if (menuParentTitle == null) {
            log.info("跳过插件 {}: menuParentTitle 为空", jarInfo.getName());
            return;
        }
        String categoryName = jarInfo.getIsFavorite() ? FAVORITE_CATEGORY_NAME : XJavaFxToolApplication.RESOURCE_BUNDLE.getString(menuParentTitle);
        PluginCategoryController category = categoryControllers.computeIfAbsent(
            categoryName, __ -> {
                PluginCategoryController _category = PluginCategoryController.newInstance(categoryName);
                addCategory(_category);
                return _category;
            }
        );

        PluginItemController item = PluginItemController.newInstance(jarInfo);
        item.setIndexController(this);
        category.addItem(item);

        if (!pluginItemControllers.contains(item)) {
            pluginItemControllers.add(item);
        }

        addMenu(jarInfo);
    }

    private void addCategory(PluginCategoryController category) {
        if (category.lblCategoryName.getText().equals(FAVORITE_CATEGORY_NAME)) {
            this.pluginCategories.getChildren().add(0, category.root);
        } else {
            this.pluginCategories.getChildren().add(category.root);
        }
    }

    private void addMenu(PluginJarInfo jarInfo) {
        MenuItem menu = moreToolsMenu.getItems().stream().filter(menuItem1 -> jarInfo.getMenuParentId().equals(menuItem1.getId())).findAny().orElse(null);
        if (menu == null) {
            menu = new Menu(XJavaFxToolApplication.RESOURCE_BUNDLE.getString(jarInfo.getMenuParentTitle()));
            menu.setId(jarInfo.getMenuParentId());
            moreToolsMenu.getItems().add(menu);
        }
        MenuItem menuItem = new MenuItem(jarInfo.getTitle());
        if (jarInfo.getIconImage() != null || StringUtils.isNotEmpty(jarInfo.getIconPath())) {
            ImageView imageView = new ImageView(jarInfo.getIconImage() == null ? new Image(jarInfo.getIconPath()) : jarInfo.getIconImage());
            imageView.setFitHeight(18);
            imageView.setFitWidth(18);
            menuItem.setGraphic(imageView);
        }
        menuItem.setOnAction((ActionEvent event) -> indexService.loadPlugin(jarInfo));
        ((Menu) menu).getItems().add(menuItem);
        menuItemMap.put(menuItem.getText(), menuItem);
    }

    public void selectAction(String selectText) {
        boolean notSearching = StringUtils.isBlank(selectText);
        pluginItemControllers.forEach(itemController -> itemController.setVisible(notSearching || itemController.matchKeyword(selectText)));
    }

    @FXML
    private void exitAction() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void closeAllTabAction() {
        tabPaneMain.getTabs().removeIf(Tab::isClosable);
    }

    @FXML
    private void openAllTabAction() {
        for (MenuItem value : menuItemMap.values()) {
            value.fire();
        }
    }

    @FXML
    private void addNodepadAction(ActionEvent event) {
        indexService.addNodepadAction(event);
    }

    @FXML
    private void addLogConsoleAction(ActionEvent event) {
        indexService.addLogConsoleAction(event);
    }

    @FXML
    private void pluginManageAction() throws Exception {
        new FxDialog<PluginManageController>()
            .setBodyFxml(PluginManageController.FXML)
            .setOwner(FxApp.primaryStage)
            .setResizable(true)
            .setTitle(XJavaFxToolApplication.RESOURCE_BUNDLE.getString("plugin_manage"))
            .setPrefWidth(800)
            .withStage(stage -> stage.setOnCloseRequest(event -> loadPlugins()))
            .show();
    }

    @FXML
    private void SettingAction() {
        SystemSettingService.openSystemSettings(bundle.getString("Setting"));
    }

    @FXML
    private void checkerVersionAction() {
        if (!VersionChecker.checkNewVersion()) {
            FxAlerts.info("提示", "已经是新版本");
        }
    }

    @FXML
    private void aboutAction() {
        AlertUtil.showInfoAlert(bundle.getString("aboutText") + Config.xJavaFxToolVersions);
    }

    @FXML
    private void setLanguageAction(ActionEvent event) throws Exception {
        MenuItem menuItem = (MenuItem) event.getSource();
        indexService.setLanguageAction(menuItem.getText());
    }

    @FXML
    private void openLogFileAction() {
        String filePath = "logs/logFile.log";
        JavaFxSystemUtil.openDirectory(filePath);
    }

    @FXML
    private void openLogFolderAction() {
        JavaFxSystemUtil.openDirectory("logs/");
    }

    @FXML
    private void openConfigFolderAction() {
        JavaFxSystemUtil.openDirectory(ConfigureUtil.getConfigurePath());
    }

    @FXML
    private void openPluginFolderAction() {
        JavaFxSystemUtil.openDirectory("libs/");
    }
    
    @FXML
    private void openDevPluginFolderAction() {
        JavaFxSystemUtil.openDirectory("devLibs/");
    }

    @FXML
    private void xwintopLinkOnAction() throws Exception {
        HttpClientUtil.openBrowseURLThrowsException("https://gitee.com/xwintop/xJavaFxTool");
    }

    @FXML
    private void userSupportAction() throws Exception {
        HttpClientUtil.openBrowseURLThrowsException("https://support.qq.com/product/127577");
    }
}
