package com.xwintop.xcore.javafx.control;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.*;

import com.xwintop.xcore.util.KeyValue;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ToggleGridTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 创建并填充内容
        ToggleGrid<Integer, Integer> toggleGrid = new ToggleGrid<>(35, 35);
        for (int i = 0; i < 100; i++) {
            toggleGrid.addCell(new KeyValue<>(i, i));
        }

        // 底部指示
        Label lblSelection = label("已选择的内容：");
        lblSelection.setWrapText(true);
        toggleGrid.addSelectionUpdatedListener(() -> {
            lblSelection.setText("已选择的内容：" + toggleGrid.getSelectedValues());
        });

        // 界面布局
        primaryStage.setScene(new Scene(vbox(30, 10,
            hbox(0, 10,
                label("鼠标拖拽来选择："),
                hyperlink("全选", () -> toggleGrid.select(i -> true)),
                hyperlink("清空", toggleGrid::clearSelection),
                hyperlink("选择奇数", () -> toggleGrid.select(i -> i.getValue() % 2 == 1))
            ),
            toggleGrid,
            lblSelection
        ), 550, 500));

        // label 控件想要自动换行，就必须设置 maxWidth
        primaryStage.setOnShown(event -> {
            lblSelection.maxWidthProperty().bind(toggleGrid.widthProperty());
        });

        primaryStage.show();
    }
}