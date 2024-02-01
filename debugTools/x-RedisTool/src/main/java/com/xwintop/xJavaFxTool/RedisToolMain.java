package com.xwintop.xJavaFxTool;

import com.xwintop.xJavaFxTool.utils.VersionChecker;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class RedisToolMain extends Application {
    private static final String VERSION = "0.0.2";
    private static final String CHEKC_URL = "https://gitee.com/api/v5/repos/xwintop/x-RedisTool/releases/latest";
    private static final String DOWNLOAD_URL = "https://gitee.com/xwintop/x-RedisTool/releases";


    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fXMLLoader = RedisToolMain.getFXMLLoader();
        ResourceBundle resourceBundle = fXMLLoader.getResources();
        Parent root = fXMLLoader.load();
        primaryStage.setResizable(true);
        primaryStage.setTitle(resourceBundle.getString("Title") + " v" + VERSION);
        primaryStage.getIcons().add(new Image("/images/RedisToolIcon.png"));
        double[] screenSize = JavaFxSystemUtil.getScreenSizeByScale(0.74D, 0.8D);
        primaryStage.setScene(new Scene(root, screenSize[0], screenSize[1]));
        primaryStage.setOnShown(e -> VersionChecker.checkerVersion(CHEKC_URL, DOWNLOAD_URL, VERSION));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    public static FXMLLoader getFXMLLoader() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.RedisTool");
        URL url = Object.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/debugTools/redisTool/RedisTool.fxml");
        FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
        return fXMLLoader;
    }
}