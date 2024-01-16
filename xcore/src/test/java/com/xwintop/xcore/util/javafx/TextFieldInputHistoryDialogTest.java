package com.xwintop.xcore.util.javafx;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.button;
import static com.xwintop.xcore.javafx.helper.LayoutHelper.hbox;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TextFieldInputHistoryDialogTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(hbox(30, 10, Pos.CENTER,
            button("打开对话框", this::openDialog)
        ), 400, 300));
        primaryStage.show();
    }

    private void openDialog() {
        final TextFieldInputHistoryDialog dialog =
            new TextFieldInputHistoryDialog("target/text-field-input-history-dialog-config.txt", "A", "B", "C");

        dialog.openEditWindow();
    }
}