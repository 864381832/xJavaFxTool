package com.xwintop.xcore.javafx.control;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

import static javafx.application.Application.launch;

public class BreadcrumbTest {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        launch(BreadcrumbTestApp.class);
    }

    public static class BreadcrumbTestApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            var listView = new ListView<String>();
            var breadcrumb = new Breadcrumb();

            // Breadcrumb 的主要用法：
            // 1、直接设置要显示的路径
            breadcrumb.setPath("根目录", "home", "user");

            // 2、侦听路径变更事件
            breadcrumb.addEventHandler(Breadcrumb.PATH_CHANGED, event -> {
                System.out.println("path changed: " + event.getPath());
                updateListView(listView);
            });

            listView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    var value = listView.getSelectionModel().getSelectedItem();
                    if (value != null) {
                        // 3. 在当前路径下添加下级路径
                        breadcrumb.appendPath(value);
                    }
                }
            });

            var root = new BorderPane();
            root.setPadding(new Insets(10));
            root.setTop(breadcrumb);
            root.setCenter(listView);

            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
            updateListView(listView);
        }

        private void updateListView(ListView<String> listView) {
            listView.getItems().clear();
            for (int i = 0; i < RANDOM.nextInt(10, 20); i++) {
                listView.getItems().add(RandomStringUtils.randomAlphabetic(RANDOM.nextInt(5, 15)));
            }
        }

    }
}
