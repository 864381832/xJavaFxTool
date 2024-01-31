package com.xwintop.xJavaFxTool;

import com.xwintop.xJavaFxTool.controller.littleTools.LogupFrameController;
import com.xwintop.xJavaFxTool.controller.littleTools.SoftInformationFrameController;
import com.xwintop.xJavaFxTool.utils.SimpleTools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class BookManageSystemMain extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("图书馆管理系统 ");
        // 启动界面即为登录界面
        initLogupFrame();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 登录界面
     */
    public void initLogupFrame() {
        try {
            // 加载登录界面的fxml文件
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/xwintop/xJavaFxTool/fxmlView/littleTools/logupFrame.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("登录");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // 获取登录界面的控制器类
            LogupFrameController controller = loader.getController();
            // 将stage作为参数传递到控制器中
            controller.setStage(primaryStage);

            // 展示舞台
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 主界面
     */
    public void initMainFrame() {
        try {
            // 加载主界面
            AnchorPane root = SimpleTools.loadByFxml("/com/xwintop/xJavaFxTool/fxmlView/littleTools/mainFrame.fxml");

            // 设置stage舞台的属性
            Stage mainFrameStage = new Stage();
            mainFrameStage.setTitle("图书管理系统主界面");
            mainFrameStage.setResizable(true);
            mainFrameStage.setAlwaysOnTop(false);
            mainFrameStage.initModality(Modality.APPLICATION_MODAL);
            mainFrameStage.initOwner(primaryStage);

            Scene scene = new Scene(root);
            mainFrameStage.setScene(scene);

            mainFrameStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关于软件弹出框界面
     * @return 返回Scene
     */
    public Scene initAboutSoftFrame() {
        try {
            // 加载关于软件界面
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(BookManageSystemMain.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/littleTools/softInformationFrame.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage mainFrameStage = new Stage();
            mainFrameStage.setTitle("关于软件");
            mainFrameStage.setResizable(true);
            mainFrameStage.setAlwaysOnTop(false);
            mainFrameStage.initModality(Modality.APPLICATION_MODAL);
            mainFrameStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            mainFrameStage.setScene(scene);

            SoftInformationFrameController controller = loader.getController();
            controller.setDialogStage(mainFrameStage);

            mainFrameStage.showAndWait();
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
