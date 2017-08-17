package com.xwintop.xJavaFxTool.datafx;

import io.datafx.controller.flow.Flow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Tutorial1Main extends Application {
	 
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) throws Exception {
    	Flow flow = new Flow(SimpleController.class);
    	flow.startInStage(primaryStage);
    }
}
