package com.xwintop.xJavaFxTool;

import com.jfoenix.controls.JFXDecorator;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.Config.Keys;
import com.xwintop.xJavaFxTool.utils.StageUtils;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: Main
 * @Description: 启动类
 * @author: xufeng
 * @date: 2017年11月10日 下午4:34:11
 */
@Slf4j
public class Main extends Application {

    public static final String LOGO_PATH = "/images/icon.jpg";

    public static ResourceBundle RESOURCE_BUNDLE;

    private static Stage stage;

    public static void main(String[] args) {

        XJavaFxSystemUtil.initSystemLocal();    // 初始化本地语言
        XJavaFxSystemUtil.addJarByLibs();       // 添加外部jar包

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // 初始化 JavaFX 全局设置
        FxApp.init(primaryStage, LOGO_PATH);
        FxApp.setupIcon(primaryStage);
        FxApp.styleSheets.add(Main.class.getResource("/css/jfoenix-main.css").toExternalForm());

        primaryStage.setResizable(true);
        primaryStage.setTitle(RESOURCE_BUNDLE.getString("Title") + Config.xJavaFxToolVersions);
        primaryStage.setOnCloseRequest(this::confirmExit);

        if (Config.getBoolean(Keys.NewLauncher, false)) {
            loadNewUI(primaryStage);
        } else {
            loadClassicUI(primaryStage);
        }

        StageUtils.loadPrimaryStageBound(primaryStage);
        primaryStage.show();
        StageUtils.updateStageStyle(primaryStage);
    }

    private void loadNewUI(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/newui/main.fxml"));
        fxmlLoader.setResources(RESOURCE_BUNDLE);

        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
    }

    private void loadClassicUI(Stage primaryStage) throws IOException {
        FXMLLoader fXMLLoader = IndexController.getFXMLLoader();
        Parent root = fXMLLoader.load();

        JFXDecorator decorator = JavaFxViewUtil.getJFXDecorator(
            primaryStage,
            RESOURCE_BUNDLE.getString("Title") + Config.xJavaFxToolVersions,
            LOGO_PATH,
            root
        );
        decorator.setOnCloseButtonAction(() -> confirmExit(null));

        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(decorator);
        primaryStage.setScene(scene);
    }

    private void confirmExit(Event event) {
        if (Config.getBoolean(Keys.ConfirmExit, true)) {
            if (FxAlerts.confirmYesNo("退出应用", "确定要退出吗？")) {
                doExit();
            } else if (event != null) {
                event.consume();
            }
        } else {
            doExit();
        }
    }

    private void doExit() {
        StageUtils.savePrimaryStageBound(stage);
        Platform.exit();
        System.exit(0);
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }
}
