package com.xwintop.xcore.javafx.helper;

import static com.xwintop.xcore.javafx.helper.ImageViewHelper.imageView;
import static com.xwintop.xcore.javafx.helper.LayoutHelper.hbox;
import static com.xwintop.xcore.javafx.helper.LayoutHelper.label;
import static com.xwintop.xcore.javafx.helper.MenuHelper.*;

import com.xwintop.xcore.javafx.wrapper.ContextMenuWrapper;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ContextMenuHelperTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        final String iconPath = "classpath:/icon.png";

        ContextMenuWrapper menuWrapper = ContextMenuWrapper.of(contextMenu(
            menuItem("打开...", () -> imageView(iconPath, 16), () -> {}),
            menuItem("编辑", () -> {}),
            separator(),
            menuItem("保存", imageView(iconPath, 16), () -> {}),
            menuItem("另存为...", () -> {}),
            separator(),
            menuItem("删除", imageView(iconPath, 16), () -> {})
        ));

        primaryStage.setScene(new Scene(
            menuWrapper.wrap(hbox(30, 0, Pos.CENTER, label("右键点击这里"))),
            400, 300));
        primaryStage.show();
    }
}