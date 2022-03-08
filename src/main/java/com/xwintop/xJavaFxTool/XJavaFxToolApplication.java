package com.xwintop.xJavaFxTool;

import com.jfoenix.controls.JFXDecorator;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.Config.Keys;
import com.xwintop.xJavaFxTool.utils.StageUtils;
import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.dialog.FxAlerts;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * JavaFX 入口，从 Java 9+ 开始入口类不应再包含 main 方法。
 */
@Slf4j
public class XJavaFxToolApplication extends Application {

    public static final String LOGO_PATH = "/images/icon.jpg";

    public static ResourceBundle RESOURCE_BUNDLE;

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // 初始化 JavaFX 全局设置
        FxApp.init(primaryStage, LOGO_PATH);
        FxApp.setupIcon(primaryStage);
        FxApp.styleSheets.add(XJavaFxToolApplication.class.getResource("/css/jfoenix-main.css").toExternalForm());

        primaryStage.setResizable(true);
        primaryStage.setTitle(RESOURCE_BUNDLE.getString("Title") + Config.xJavaFxToolVersions);
        primaryStage.setOnCloseRequest(this::confirmExit);

        // 只启用新UI，因为：
        // 1. 新UI启动时不扫描插件目录，启动更快；
        // 2. 新UI使用独立的ClassLoader加载插件，兼容性更好；
        // 3. 新UI本身体验较好。
//        loadNewUI(primaryStage);
        loadClassicUI(primaryStage);

        StageUtils.loadPrimaryStageBound(primaryStage);
        primaryStage.show();
        StageUtils.updateStageStyle(primaryStage);
    }

    private void loadNewUI(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(XJavaFxToolApplication.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/newui/main.fxml"));
        fxmlLoader.setResources(RESOURCE_BUNDLE);

        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
    }

    private void loadClassicUI(Stage primaryStage) throws IOException {
        FXMLLoader fXMLLoader = IndexController.getFXMLLoader();
        Parent root = fXMLLoader.load();
        primaryStage.setScene(new Scene(root));
//        JFXDecorator decorator = JavaFxViewUtil.getJFXDecorator(
//            primaryStage,
//            RESOURCE_BUNDLE.getString("Title") + Config.xJavaFxToolVersions,
//            LOGO_PATH,
//            root
//        );
//        decorator.setOnCloseButtonAction(() -> confirmExit(null));
//
//        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(decorator);
//        primaryStage.setScene(scene);
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
        XJavaFxToolApplication.stage = stage;
    }
}
