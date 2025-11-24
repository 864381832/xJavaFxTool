package com.xwintop.xcore.javafx.helper;

import com.xwintop.xcore.javafx.wrapper.BorderWrapper;
import com.xwintop.xcore.javafx.wrapper.BorderWrapper.BorderStyle;
import com.xwintop.xcore.javafx.wrapper.SizeWrapper;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.stream.Collectors;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.vbox;

public class DropContentHelperTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderWrapper border = BorderWrapper.of("#666666", BorderStyle.dashed, 2);
        SizeWrapper size = SizeWrapper.of(100, 100);

        ///////////////////////////////////////////////////////////////

        Label dropTextLabel = new Label("拖放任意文本内容到这里");
        VBox dropTextBox = vbox(0, 0, Pos.CENTER, dropTextLabel);
        optimizeLabel(dropTextLabel, dropTextBox);

        DropContentHelper.acceptText(dropTextBox, (vBox, s) -> dropTextLabel.setText(s));

        ///////////////////////////////////////////////////////////////

        Label dropFileLabel = new Label("拖放任意文件到这里");
        VBox dropFileBox = vbox(0, 0, Pos.CENTER, dropFileLabel);
        optimizeLabel(dropFileLabel, dropFileBox);

        DropContentHelper.acceptFile(dropFileBox,
            (vBox, f) -> dropFileLabel.setText(
                f.stream().map(File::getAbsolutePath).collect(Collectors.joining("\n"))
            ));

        ///////////////////////////////////////////////////////////////

        primaryStage.setScene(new Scene(vbox(20, 20,
            border.wrap(size.wrapPref(dropTextBox)),
            border.wrap(size.wrapPref(dropFileBox))
        )));

        primaryStage.setAlwaysOnTop(true);
        primaryStage.setWidth(500);
        primaryStage.setHeight(350);
        primaryStage.show();
    }

    private void optimizeLabel(Label dropTextLabel, Region dropTextBox) {
        dropTextLabel.maxWidthProperty().bind(dropTextBox.widthProperty());
        dropTextLabel.setWrapText(true);
    }
}