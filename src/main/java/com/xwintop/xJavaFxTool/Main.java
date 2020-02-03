package com.xwintop.xJavaFxTool;

import com.jfoenix.controls.JFXDecorator;
import com.xwintop.xJavaFxTool.controller.IndexController;
import com.xwintop.xJavaFxTool.utils.Config;
import com.xwintop.xJavaFxTool.utils.StageUtils;
import com.xwintop.xJavaFxTool.utils.XJavaFxSystemUtil;
import com.xwintop.xcore.util.javafx.AlertUtil;
import com.xwintop.xcore.util.javafx.JavaFxViewUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
        JFXDecorator decorator = JavaFxViewUtil.getJFXDecorator(primaryStage, resourceBundle.getString("Title") + Config.xJavaFxToolVersions, "/images/icon.jpg", root);
        decorator.setOnCloseButtonAction(() -> {
            if (AlertUtil.showConfirmAlert("确定要退出吗？")) {
                System.exit(0);
            }
        });
        Scene scene = JavaFxViewUtil.getJFXDecoratorScene(decorator);
//		Scene scene = new Scene(root);
        primaryStage.setResizable(true);
        primaryStage.setTitle(resourceBundle.getString("Title"));//标题
        primaryStage.getIcons().add(new Image("/images/icon.jpg"));//图标
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (AlertUtil.showConfirmAlert("确定要退出吗？")) {
                    System.exit(0);
                } else {
                    event.consume();
                }
            }
        });

        StageUtils.updateStageStyle(primaryStage);
        stage = primaryStage;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Main.stage = stage;
    }
}