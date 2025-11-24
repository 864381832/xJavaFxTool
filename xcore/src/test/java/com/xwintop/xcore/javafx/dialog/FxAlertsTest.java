package com.xwintop.xcore.javafx.dialog;

import com.xwintop.xcore.javafx.FxApp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.xwintop.xcore.javafx.helper.LayoutHelper.button;
import static com.xwintop.xcore.javafx.helper.LayoutHelper.vbox;

public class FxAlertsTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxApp.init(primaryStage, "/icon.png");
        primaryStage.setScene(new Scene(
            vbox(100, 20,
                button("信息对话框", this::alertInfo),
                button("警告对话框", this::alertWarn),
                button("错误对话框", this::alertError),
                button("异常对话框", this::alertException)
            )
        ));

        FxAlerts.error("错误", "主窗体还没有显示之前的错误提示");

        primaryStage.show();
    }

    private void alertInfo() {
        FxAlerts.info("信息", "任务已经完成。");
    }

    private void alertWarn() {
        FxAlerts.warn("警告", "本网站仅允许18岁以上成年人访问。");
    }

    private void alertError() {
        FxAlerts.error("错误", "用户余额不足，无法提现。");
    }

    private void alertException() {
        FxAlerts.error("提现失败", new IllegalStateException("User has no balance."));
    }
}