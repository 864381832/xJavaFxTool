package com.xwintop.xJavaFxTool;

import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.helper.FxmlHelper;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdiomDictMain extends Application {

    public static final String LOGO_PATH = "/com/xwintop/xJavaFxTool/fxmlView/assistTools/logo.png";

    public static final String FXML_PATH = "/com/xwintop/xJavaFxTool/fxmlView/assistTools/idiom-dict-main.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FxApp.init(primaryStage, LOGO_PATH);
        FxApp.setupIcon(primaryStage);

        primaryStage.setTitle("成语字典");
        primaryStage.setWidth(500);
        primaryStage.setHeight(350);

        FxmlHelper.loadIntoStage(FXML_PATH, primaryStage).show();
    }
}