package com.xwintop.xJavaFxTool;

import com.xwintop.xcore.javafx.FxApp;
import com.xwintop.xcore.javafx.helper.FxmlHelper;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextToSpeechToolMain extends Application {

    public static final String LOGO_PATH = "/com/xwintop/xJavaFxTool/fxmlView/assistTools/text-to-speech-tool.png";

    public static final String FXML_PATH = "/com/xwintop/xJavaFxTool/fxmlView/assistTools/TextToSpeechTool.fxml";

    public static final ResourceBundle RES = ResourceBundle.getBundle("locale.TextToSpeechTool");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FxApp.init(primaryStage, LOGO_PATH);
        FxApp.setupIcon(primaryStage);

        primaryStage.setTitle(RES.getString("Title"));
        primaryStage.setWidth(700);
        primaryStage.setHeight(400);

        FxmlHelper.loadIntoStage(FXML_PATH, RES.getBaseBundleName(), primaryStage).show();
    }
}