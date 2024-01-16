package com.xwintop.xcore.javafx.helper;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.hbox;

import com.xwintop.xcore.util.KeyValue;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class ChoiceBoxHelperTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setPrefWidth(100);
        choiceBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("Selected: " + newValue);
        }));

        List<KeyValue<String, String>> list = new ArrayList<>();
        list.add(new KeyValue<>("name1", "value1"));
        list.add(new KeyValue<>("name2", "value2"));
        list.add(new KeyValue<>("name3", "value3"));
        ChoiceBoxHelper.setContentDisplay(choiceBox, list);

        primaryStage.setScene(new Scene(hbox(30, 0, Pos.CENTER, choiceBox), 300, 100));
        primaryStage.show();
    }
}