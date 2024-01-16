package com.xwintop.xcore.javafx.control;

import com.xwintop.xcore.javafx.helper.FxmlHelper;
import javafx.application.Application;
import javafx.stage.Stage;

public class IntegerSpinnerTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxmlHelper
            .loadIntoStage("/integer-spinner-test.fxml", primaryStage)
            .show();
    }
}