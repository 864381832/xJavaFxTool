package com.xwintop.xcore.javafx.helper;

import com.xwintop.xcore.javafx.wrapper.BorderWrapper;
import com.xwintop.xcore.javafx.wrapper.BorderWrapper.BorderStyle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static com.xwintop.xcore.javafx.wrapper.BorderWrapper.of;

public class LayoutHelperTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderWrapper blueBorder = of("#44AAFF", BorderStyle.solid, 2);

        primaryStage.setScene(new Scene(new BorderPane(
            blueBorder.wrap(new Label("Hello!"))
        )));

        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
    }
}