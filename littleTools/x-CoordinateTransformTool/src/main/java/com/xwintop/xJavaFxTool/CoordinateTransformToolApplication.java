package com.xwintop.xJavaFxTool;

import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class CoordinateTransformToolApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fXMLLoader = CoordinateTransformToolApplication.getFXMLLoader();
        ResourceBundle resourceBundle = fXMLLoader.getResources();
        Parent root = fXMLLoader.load();
        primaryStage.setResizable(true);
        primaryStage.setTitle(resourceBundle.getString("Title"));
//        primaryStage.getIcons().add(new Image("/images/icon.jpg"));
        double[] screenSize = JavaFxSystemUtil.getScreenSizeByScale(0.74, 0.8);
        primaryStage.setScene(new Scene(root, screenSize[0], screenSize[1]));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }

    public static FXMLLoader getFXMLLoader() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.CoordinateTransformTool");
        URL url = CoordinateTransformToolApplication.class.getClassLoader().getResource("com/xwintop/xJavaFxTool/fxmlView/littleTools/CoordinateTransformTool.fxml");
        return new FXMLLoader(url, resourceBundle);
    }
}