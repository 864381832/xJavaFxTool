package com.xwintop.xcore.util.javafx;

import com.xwintop.xcore.javafx.FxApp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.button;
import static com.xwintop.xcore.javafx.helper.LayoutHelper.vbox;
import static com.xwintop.xcore.util.javafx.AlertUtil.*;

public class AlertUtilTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxApp.init(primaryStage, "/icon.png");
        primaryStage.setScene(new Scene(
            vbox(10, 10,
                button("打开 info 对话框", this::alertInfo),
                button("打开 confirm 对话框(是/否)", this::alertConfirm),
                button("打开 confirm 对话框(确定/取消)", this::alertConfirm2),
                button("打开 confirm 对话框(是/否/取消)", this::alertConfirm3),
                button("打开 input 对话框", this::alertInput)
            ),
            400, 300
        ));
        primaryStage.show();
    }

    private void alertInput() {
        String result = showInputAlertDefaultValue(""
            + "意大利疫情扩散，部分地区采取“武汉式封城”，"
            + "德国之声记者来到距离隔离区不远的北部村庄Vittadone，"
            + "那里的居民目前处在怎样的生活状态？", "hahahahahahahahaha");

        showInfoAlert("你输入了\n" + result);
    }

    private void alertConfirm() {
        if (confirmYesNo("确认", "黑白橘子能有黑白桃子那么中二么？？")) {
            showInfoAlert("你选择了是。");
        } else {
            showInfoAlert("你选择了否。");
        }
    }

    private void alertConfirm2() {
        if (confirmOkCancel("确认", "黑白橘子能有黑白桃子那么中二么？？")) {
            showInfoAlert("你选择了确定。");
        } else {
            showInfoAlert("你选择了取消。");
        }
    }

    private void alertConfirm3() {
        ButtonType buttonType = confirmYesNoCancel("确认", "黑白橘子能有黑白桃子那么中二么？？");
        showInfoAlert("你选择了" + buttonType.getText() + "。");
    }

    private void alertInfo() {
        showInfoAlert("华为这里面什么都没有啊");
    }
}