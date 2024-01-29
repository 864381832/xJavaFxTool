package com.xwintop.xJavaFxTool;

import com.xwintop.xcore.javafx.FxApp;
import java.net.URL;
import java.util.ResourceBundle;

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

@Slf4j
public class QrCodeBuilderMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxApp.init(primaryStage, null);
        FXMLLoader fXMLLoader = QrCodeBuilderMain.getFXMLLoader();
        ResourceBundle resourceBundle = fXMLLoader.getResources();
        Parent root = fXMLLoader.load();
        primaryStage.setResizable(true);
        primaryStage.setTitle(resourceBundle.getString("Title"));
        primaryStage.getIcons().add(new Image("/images/QRCodeBuilderIcon.png"));
        double[] screenSize = JavaFxSystemUtil.getScreenSizeByScale(0.74D, 0.8D);
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
        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.QRCodeBuilder");
        URL url = Object.class.getResource("/com/xwintop/xJavaFxTool/fxmlView/littleTools/QRCodeBuilder.fxml");
        return new FXMLLoader(url, resourceBundle);
    }
}