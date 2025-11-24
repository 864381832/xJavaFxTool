package com.xwintop.xcore.javafx.dialog;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimpleFormDialogTest {

    public static void main(String[] args) {
        Application.launch(SimpleFormDialogTestApp.class, args);
    }

    public static class SimpleFormDialogTestApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Button button = new Button("打开对话框");
            button.setOnAction(event -> {
                openSimpleFormDialog();
            });

            primaryStage.setScene(new Scene(new BorderPane(button), 300, 250));
            primaryStage.show();
        }

        private void openSimpleFormDialog() {
            var nameField = new SimpleFormDialog.Field<>(SimpleFormDialog.FieldType.TEXT, "姓名", "张三");
            var okField = new SimpleFormDialog.Field<>(SimpleFormDialog.FieldType.CHECKBOX, "是否好了", true);
            var genderField = new SimpleFormDialog.Field<>(SimpleFormDialog.FieldType.COMBOBOX, "性别", "男", "男", "女");
            var textAreaField = new SimpleFormDialog.Field<>(SimpleFormDialog.FieldType.TEXTAREA, "备注", "这是一个备注");

            SimpleFormDialog simpleFormDialog = new SimpleFormDialog();
            simpleFormDialog.setTitle("标题");
            simpleFormDialog.addFields(nameField, okField, genderField, textAreaField);
            var result = simpleFormDialog.showAndWait();

            System.out.println(textAreaField.getValue());
            System.out.println("result = " + result);
        }
    }
}
