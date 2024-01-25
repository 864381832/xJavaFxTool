package com.xwintop.xJavaFxTool;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class EncryptAndDecryptApplication extends Application {
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fXMLLoader = EncryptAndDecryptApplication.getFXMLLoader();
//        ResourceBundle resourceBundle = fXMLLoader.getResources();
        Parent root = fXMLLoader.load();
        primaryStage.setResizable(true);
//        primaryStage.setTitle(resourceBundle.getString("Title"));
//        primaryStage.getIcons().add(new Image("/images/icon.jpg"));
//        double[] screenSize = JavaFxSystemUtil.getScreenSizeByScale(0.74D, 0.8D);
        double[] screenSize = new double[]{720d,1280d};
        primaryStage.setScene(new Scene(root, screenSize[0], screenSize[1]));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    public static FXMLLoader getFXMLLoader() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.EncryptAndDecrypt");
        URL url = EncryptAndDecryptApplication.class.getClassLoader().getResource("com/xwintop/xJavaFxTool/fxmlView/littleTools/EncryptAndDecrypt.fxml");
        FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
        return fXMLLoader;
    }
}