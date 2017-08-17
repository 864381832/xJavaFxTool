package com.xwintop.xJavaFxTool.datafx.flow3;

import javafx.application.Application;
import javafx.stage.Stage;
import io.datafx.controller.flow.Flow;

/**
 * Main class for the tutorial. This application will show only a simple wizard in the given Stage / Window.
 * The views of the wizard are defined by the DataFX controller API and shown in a DataFX flow.
 * As shown in tutorial 1 and 2 the startInStage() method of the Flow class is used to visualize the wizard on screen.
 * The first view of the wizard is defined by the WizardStartController class.
 */
public class Tutorial3Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Flow(WizardStartController.class).startInStage(primaryStage);
    }
}

