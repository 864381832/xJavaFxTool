package com.xwintop.xJavaFxTool;

import com.xwintop.xJavaFxTool.controller.games.GameScreen;
import com.xwintop.xJavaFxTool.controller.games.core.FFContants;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SnakeMain extends Application {
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group mGroup = new Group();
        Scene mScene = new Scene(mGroup, FFContants.WIDTH, FFContants.HEIGHT);
        GameScreen gameScreen = new GameScreen(FFContants.WIDTH, FFContants.HEIGHT);
        mGroup.getChildren().add(gameScreen);
        gameScreen.start();
        gameScreen.initEvents();
        mScene.setFill(Color.BLACK);
        primaryStage.setResizable(true);
        primaryStage.setTitle("JavaFX游戏开发 贪吃蛇");
        primaryStage.setScene(mScene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

}