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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.ResourceBundle;

/**
 * @ClassName: Main
 * @Description: 启动类
 * @author: xufeng
 * @date: 2017年11月10日 下午4:34:11
 */
@Slf4j
public class Main extends Application {

    public static final String LOGO_PATH = "/images/icon.jpg";

    private static Stage stage;

    public static void main(String[] args) {
        try {
            log.info("xJavaFxTool项目启动");
            launch(args);
        } catch (Exception e) {
            log.error("xJavaFxTool启动失败：", e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 初始化 JavaFX 全局设置
        FxApp.init(primaryStage, LOGO_PATH);
        FxApp.styleSheets.add(Main.class.getResource("/css/jfoenix-main.css").toExternalForm());

        XJavaFxSystemUtil.initSystemLocal();//初始化本地语言
        XJavaFxSystemUtil.addJarByLibs();//添加外部jar包

        FXMLLoader fXMLLoader = IndexController.getFXMLLoader();
        ResourceBundle resourceBundle = fXMLLoader.getResources();
        Parent root = fXMLLoader.load();

        JFXDecorator decorator = JavaFxViewUtil.getJFXDecorator(
            primaryStage,
            resourceBundle.getString("Title") + Config.xJavaFxToolVersions,
            LOGO_PATH,
            root
        );
        decorator.setOnCloseButtonAction(() -> confirmExit(null));

        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(decorator);

        primaryStage.setResizable(true);
        primaryStage.setTitle(resourceBundle.getString("Title"));//标题
        primaryStage.getIcons().add(new Image(LOGO_PATH));//图标
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(this::confirmExit);

        StageUtils.loadPrimaryStageBound(primaryStage);
        primaryStage.show();

        StageUtils.updateStageStyle(primaryStage);
        stage = primaryStage;
    }

    private void confirmExit(Event event) {
        if (Config.getBoolean(Keys.ConfirmExit, true)) {
            if (FxAlerts.confirmYesNo("退出应用", "确定要退出吗？")) {
                StageUtils.savePrimaryStageBound(stage);
                Platform.exit();
                System.exit(0);
            } else if (event != null) {
                event.consume();
            }
        } else {
            StageUtils.savePrimaryStageBound(stage);
            Platform.exit();
            System.exit(0);
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }
}
