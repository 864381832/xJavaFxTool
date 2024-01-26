package com.xwintop.xJavaFxTool;

import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.helper.FxmlHelper;
import com.xwintop.xcore.util.javafx.JavaFxSystemUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ResourceBundle;

@Slf4j
public class ImageToolApplication extends Application {

    public static final String LOGO_PATH = "images/ImageToolIcon.png";

    public static final String FXML_PATH = "com/xwintop/xJavaFxTool/fxmlView/littleTools/image-tool-main.fxml";

    public static final ResourceBundle RES = ResourceBundle.getBundle("locale.ImageTool");

    @Override
    public void start(Stage primaryStage) {
        FxApp.init(primaryStage, LOGO_PATH);
        FxApp.setupIcon(primaryStage);

        primaryStage.setTitle(RES.getString("Title"));
        double[] screenSize = JavaFxSystemUtil.getScreenSizeByScale(0.74D, 0.8D);
        primaryStage.setWidth(screenSize[0]);
        primaryStage.setHeight(screenSize[1]);

        FxmlHelper.loadIntoStage(FXML_PATH, RES.getBaseBundleName(), primaryStage).show();
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            Platform.exit();
            System.exit(0);
        });
    }

}