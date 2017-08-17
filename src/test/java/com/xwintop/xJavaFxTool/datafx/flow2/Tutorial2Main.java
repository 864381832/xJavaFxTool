package com.xwintop.xJavaFxTool.datafx.flow2;

import io.datafx.controller.flow.Flow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Tutorial2Main extends Application {
	 
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) throws Exception {
        new Flow(View1Controller.class).startInStage(primaryStage);
    }
}