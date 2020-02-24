package com.xwintop.xJavaFxTool;

import com.jfoenix.controls.JFXDecorator;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.Config.Keys.MainWindow;
import com.xwintop.xJavaFxTool.utils.StageUtils;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

    public static final String PREFERENCE_ROOT = "com.xwintop.xJavaFxTool";

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
        XJavaFxSystemUtil.initSystemLocal();//初始化本地语言
        XJavaFxSystemUtil.addJarByLibs();//添加外部jar包

        FXMLLoader fXMLLoader = IndexController.getFXMLLoader();
        ResourceBundle resourceBundle = fXMLLoader.getResources();
        Parent root = fXMLLoader.load();

        JFXDecorator decorator = JavaFxViewUtil.getJFXDecorator(
            primaryStage,
            resourceBundle.getString("Title") + Config.xJavaFxToolVersions,
            "/images/icon.jpg",
            root
        );
        decorator.setOnCloseButtonAction(() -> confirmExit(null));

        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(decorator);

        primaryStage.setResizable(true);
        primaryStage.setTitle(resourceBundle.getString("Title"));//标题
        primaryStage.getIcons().add(new Image("/images/icon.jpg"));//图标
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(this::confirmExit);

        loadPrimaryStageBound(primaryStage);
        primaryStage.show();

        StageUtils.updateStageStyle(primaryStage);
        stage = primaryStage;
    }

    private void loadPrimaryStageBound(Stage stage) {
        double left = getPreferences().getDouble(MainWindow.LEFT, -1);
        double top = getPreferences().getDouble(MainWindow.TOP, -1);
        double width = getPreferences().getDouble(MainWindow.WIDTH, -1);
        double height = getPreferences().getDouble(MainWindow.HEIGHT, -1);

        if (left > 0) {
            stage.setX(left);
        }
        if (top > 0) {
            stage.setY(top);
        }
        if (width > 0) {
            stage.setWidth(width);
        }
        if (height > 0) {
            stage.setHeight(height);
        }
    }

    private Preferences getPreferences() {
        return Preferences.userRoot().node(PREFERENCE_ROOT);
    }

    private void confirmExit(Event event) {
        if (AlertUtil.showConfirmAlert("确定要退出吗？")) {
            savePrimaryStageBound();
            Platform.exit();
            System.exit(0);
        } else if (event != null) {
            event.consume();
        }
    }

    private void savePrimaryStageBound() {
        if (stage == null || stage.isIconified()) {
            return;
        }

        getPreferences().putDouble(MainWindow.LEFT, stage.getX());
        getPreferences().putDouble(MainWindow.TOP, stage.getY());
        getPreferences().putDouble(MainWindow.WIDTH, stage.getWidth());
        getPreferences().putDouble(MainWindow.HEIGHT, stage.getHeight());
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }
}