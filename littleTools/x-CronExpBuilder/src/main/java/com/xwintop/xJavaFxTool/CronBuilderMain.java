package com.xwintop.xJavaFxTool;

import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.helper.FxmlHelper;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CronBuilderMain extends Application {

    public static final ResourceBundle RES = ResourceBundle.getBundle("locale.CronExpBuilder");

    public static final String FXML_PATH = "/com/xwintop/xJavaFxTool/fxmlView/littleTools/CronExpBuilder.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FxApp.init(primaryStage, "/images/CronExpBuilderIcon.png");
        FxApp.setupIcon(primaryStage);

        primaryStage.setTitle(RES.getString("Title"));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(450);

        FxmlHelper.loadIntoStage(FXML_PATH, RES.getBaseBundleName(), primaryStage).show();
    }
}